import AVFoundation
import Foundation

/// Owns the two device audio streams (mic capture, call playback) and the
/// resampling glue between "whatever the device's current route natively runs
/// at" and the fixed 48 kHz mono 20 ms frame plnt-core expects.
///
/// iOS counterpart of the Android `AudioEngine` (app/.../audio/AudioEngine.kt).
/// The two important mappings:
///
/// * `AVAudioSession` category `.playAndRecord` with mode `.voiceChat` is the
///   iOS equivalent of Android's `VOICE_COMMUNICATION` source: it engages the
///   voice I/O unit (so the platform echo canceller works) and routes to a
///   Bluetooth headset over HFP/SCO when one is connected — the thing the
///   official TeamSpeak app gets wrong.
/// * Push-to-talk gates *sending*, never capture. Tearing the capture stream
///   down drops the Bluetooth link and the next press audibly clips the first
///   syllable.
final class AudioEngine: @unchecked Sendable {
    /// plnt-core only ever speaks 20 ms mono frames at this rate.
    static let coreSampleRate: Double = 48_000
    static let coreFrameSamples = 960

    /// Called on the capture queue with 960 mono float samples (20 ms @ 48 kHz).
    /// Only fires while the user is transmitting.
    var onCaptureFrame: (([Float]) -> Void)?

    /// A stream could not be brought up: "capture" or "playback". Half a call
    /// still works when one of the two fails, so this reports rather than
    /// throws — but it has to reach the user, because the symptom otherwise is
    /// a call that looks connected and carries no audio.
    var onError: ((String) -> Void)?

    private let session = AVAudioSession.sharedInstance()
    private let engine = AVAudioEngine()
    private let player = AVAudioPlayerNode()
    private let captureQueue = DispatchQueue(label: "tech.phatt.chlorophone.capture")
    private let stateLock = NSLock()
    private let playbackLock = NSLock()

    private var accumulator: [Float] = []
    private var sending = false
    private var running = false
    private var playbackPending: [Float] = []
    private var observers: [NSObjectProtocol] = []

    private let playbackFormat = AVAudioFormat(
        standardFormatWithSampleRate: AudioEngine.coreSampleRate,
        channels: 1
    )!

    // MARK: - Permission

    /// Ask for the microphone. Called when the user starts a call, not at
    /// launch — iOS wants the prompt next to the action that needs it.
    static func requestMicrophonePermission(_ completion: @escaping (Bool) -> Void) {
        if #available(iOS 17.0, *) {
            AVAudioApplication.requestRecordPermission { granted in
                DispatchQueue.main.async { completion(granted) }
            }
        } else {
            AVAudioSession.sharedInstance().requestRecordPermission { granted in
                DispatchQueue.main.async { completion(granted) }
            }
        }
    }

    static var microphonePermissionGranted: Bool {
        switch AVAudioSession.sharedInstance().recordPermission {
        case .granted: return true
        case .denied: return false
        case .undetermined: return false
        @unknown default: return false
        }
    }

    // MARK: - Lifecycle

    /// Bring the audio graph up. Throws when the session could not be
    /// configured (usually: no route, or the mic is not permitted).
    func start() throws {
        guard !running else { return }
        try configureSession()
        try buildGraph()
        installObservers()
        running = true
    }

    /// Tear the graph down and release the audio session.
    func stop() {
        guard running else { return }
        running = false
        removeObservers()
        engine.inputNode.removeTap(onBus: 0)
        engine.stop()
        if engine.attachedNodes.contains(player) {
            engine.disconnectNodeOutput(player)
            engine.detach(player)
        }
        playbackLock.lock()
        playbackPending.removeAll()
        playbackLock.unlock()
        captureQueue.sync { accumulator.removeAll() }
        try? session.setActive(false, options: .notifyOthersOnDeactivation)
    }

    var isRunning: Bool { running }

    /// Push-to-talk gate. `true` = mic open (frames are sent); `false` =
    /// muted for send purposes only. Capture keeps running either way so the
    /// Bluetooth link never drops between presses.
    func setSending(_ value: Bool) {
        stateLock.lock()
        sending = value
        stateLock.unlock()
    }

    var isSending: Bool {
        stateLock.lock()
        defer { stateLock.unlock() }
        return sending
    }

    /// Human-readable description of the current output route, for the
    /// settings screen ("Bluetooth headset", "iPhone speaker", …).
    var currentRouteDescription: String {
        let outputs = session.currentRoute.outputs
        if let name = outputs.first?.portName, !name.isEmpty { return name }
        return "iPhone"
    }

    // MARK: - Playback

    /// Feed one decoded 20 ms / 960-sample / 48 kHz mono frame from plnt-core
    /// to the speaker or headset. Called from the Rust callback thread, so it
    /// must not touch the main actor.
    func enqueuePlayback(_ samples: [Float]) {
        guard running, samples.isEmpty == false else { return }
        playbackLock.lock()
        playbackPending.append(contentsOf: samples)
        // Hold back ~3 frames (60 ms) so a late frame does not starve the
        // player node; too little buffering and the queue underruns between
        // core events.
        let threshold = AudioEngine.coreFrameSamples * 3
        var chunk: [Float] = []
        if playbackPending.count >= threshold {
            let take = (playbackPending.count / AudioEngine.coreFrameSamples) * AudioEngine.coreFrameSamples
            chunk = Array(playbackPending.prefix(take))
            playbackPending.removeFirst(take)
        }
        playbackLock.unlock()
        guard !chunk.isEmpty else { return }

        var offset = 0
        while offset < chunk.count {
            let count = min(AudioEngine.coreFrameSamples, chunk.count - offset)
            guard let pcm = AVAudioPCMBuffer(
                pcmFormat: playbackFormat,
                frameCapacity: AVAudioFrameCount(count)
            ) else { break }
            pcm.frameLength = AVAudioFrameCount(count)
            if let dst = pcm.floatChannelData?[0] {
                chunk.withUnsafeBufferPointer { src in
                    guard let base = src.baseAddress else { return }
                    memcpy(dst, base + offset, count * MemoryLayout<Float>.size)
                }
            }
            player.scheduleBuffer(pcm)
            offset += count
        }
    }

    // MARK: - Capture

    private func handleCapture(_ buffer: AVAudioPCMBuffer) {
        guard let channels = buffer.floatChannelData, buffer.frameLength > 0 else { return }
        let frames = Int(buffer.frameLength)
        let channelCount = Int(buffer.format.channelCount)
        let rate = buffer.format.sampleRate

        var mono = [Float](repeating: 0, count: frames)
        if channelCount == 1 {
            mono.withUnsafeMutableBufferPointer { dst in
                guard let base = dst.baseAddress else { return }
                base.update(from: channels[0], count: frames)
            }
        } else {
            for i in 0..<frames {
                var sum: Float = 0
                for c in 0..<channelCount { sum += channels[c][i] }
                mono[i] = sum / Float(channelCount)
            }
        }

        captureQueue.async { [weak self] in
            self?.processCapture(mono, rate: rate)
        }
    }

    private func processCapture(_ samples: [Float], rate: Double) {
        let resampled: [Float]
        if abs(rate - AudioEngine.coreSampleRate) < 1 {
            resampled = samples
        } else {
            resampled = Resampler(from: rate, to: AudioEngine.coreSampleRate).process(samples)
        }
        accumulator.append(contentsOf: resampled)
        while accumulator.count >= AudioEngine.coreFrameSamples {
            let frame = Array(accumulator.prefix(AudioEngine.coreFrameSamples))
            accumulator.removeFirst(AudioEngine.coreFrameSamples)
            stateLock.lock()
            let shouldSend = sending
            stateLock.unlock()
            if shouldSend {
                onCaptureFrame?(frame)
            }
        }
    }

    // MARK: - Session and graph

    private func configureSession() throws {
        // `.voiceChat` engages the voice I/O unit (echo cancellation) and the
        // HFP path for a Bluetooth headset — the iOS counterpart of Android's
        // VOICE_COMMUNICATION source. `.defaultToSpeaker` matches the
        // Android app's loudspeaker-by-default behaviour; a headset still wins
        // when one is connected.
        try session.setCategory(
            .playAndRecord,
            mode: .voiceChat,
            options: [.allowBluetooth, .allowBluetoothA2DP, .defaultToSpeaker]
        )
        try session.setPreferredSampleRate(AudioEngine.coreSampleRate)
        try session.setPreferredIOBufferDuration(0.01)
        try session.setActive(true, options: [])
    }

    private func buildGraph() throws {
        let input = engine.inputNode
        let inputFormat = input.outputFormat(forBus: 0)
        input.installTap(onBus: 0, bufferSize: 1024, format: inputFormat) { [weak self] buffer, _ in
            self?.handleCapture(buffer)
        }
        engine.attach(player)
        engine.connect(player, to: engine.mainMixerNode, format: playbackFormat)
        engine.prepare()
        try engine.start()
        player.play()
    }

    /// Rebuild the graph against the current route. The input node's format
    /// changes when a headset arrives or leaves, and a tap installed against
    /// the old format stops delivering audio.
    private func rebuildGraph(reason: String) {
        guard running else { return }
        engine.stop()
        engine.inputNode.removeTap(onBus: 0)
        if engine.attachedNodes.contains(player) {
            engine.disconnectNodeOutput(player)
            engine.detach(player)
        }
        do {
            try configureSession()
            try buildGraph()
        } catch {
            onError?("audio route change failed (\(reason)): \(error.localizedDescription)")
        }
    }

    // MARK: - Notifications

    private func installObservers() {
        let center = NotificationCenter.default
        observers.append(center.addObserver(
            forName: AVAudioSession.routeChangeNotification,
            object: nil,
            queue: .main
        ) { [weak self] note in
            guard let self else { return }
            let reasonValue = (note.userInfo?[AVAudioSessionRouteChangeReasonKey] as? UInt) ?? 0
            let reason = AVAudioSession.RouteChangeReason(rawValue: reasonValue) ?? .unknown
            switch reason {
            case .newDeviceAvailable, .oldDeviceUnavailable, .override, .routeConfigurationChange, .categoryChange:
                self.rebuildGraph(reason: "\(reasonValue)")
            @unknown default:
                break
            }
        })

        observers.append(center.addObserver(
            forName: AVAudioSession.interruptionNotification,
            object: nil,
            queue: .main
        ) { [weak self] note in
            guard let self else { return }
            guard let info = note.userInfo,
                  let typeValue = info[AVAudioSessionInterruptionTypeKey] as? UInt,
                  let type = AVAudioSession.InterruptionType(rawValue: typeValue)
            else { return }
            switch type {
            case .began:
                // A phone call took the session. The graph stays alive; the
                // engine pauses until the system hands it back.
                self.engine.pause()
            case .ended:
                try? self.session.setActive(true)
                try? self.engine.start()
                self.player.play()
            @unknown default:
                break
            }
        })

        observers.append(center.addObserver(
            forName: AVAudioSession.mediaServicesWereResetNotification,
            object: nil,
            queue: .main
        ) { [weak self] _ in
            self?.rebuildGraph(reason: "media services reset")
        })
    }

    private func removeObservers() {
        for observer in observers {
            NotificationCenter.default.removeObserver(observer)
        }
        observers.removeAll()
    }
}

/// Minimal linear-interpolation resampler, mono, Float PCM in / Float PCM out.
/// Good enough for voice — plnt-core's Opus encode is the actual quality
/// bottleneck, not this.
struct Resampler {
    let from: Double
    let to: Double

    func process(_ input: [Float]) -> [Float] {
        guard from != to, !input.isEmpty else { return input }
        let ratio = to / from
        let outLen = max(1, Int((Double(input.count) * ratio).rounded()))
        var out = [Float](repeating: 0, count: outLen)
        for i in 0..<outLen {
            let srcPos = Double(i) / ratio
            let idx0 = min(max(Int(srcPos), 0), input.count - 1)
            let idx1 = min(idx0 + 1, input.count - 1)
            let frac = Float(srcPos - Double(idx0))
            let s0 = input[idx0]
            let s1 = input[idx1]
            out[i] = s0 + (s1 - s0) * frac
        }
        return out
    }
}
