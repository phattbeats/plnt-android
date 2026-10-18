package com.plnt.client.stream

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.plnt.client.core.CoreEvent
import com.plnt.client.service.Diag
import org.json.JSONObject
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.MediaStreamTrack
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.RtpTransceiver
import org.webrtc.ScreenCapturerAndroid
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import org.webrtc.audio.JavaAudioDeviceModule

/**
 * Shares this phone's screen as a TS6 screen share (PHA-3289, send side).
 *
 * The protocol, as proven against a live TS6 6.0 server and desktop viewer
 * with the RAID sender probe:
 *
 *  1. `setupstream name=<title> type=3 accessibility=1 mode=1 bitrate=4608
 *     viewer_limit=0 audio=0`. The server *requires* `accessibility` (it echoes
 *     it back as `access`) and answers `notifystreamstarted clid=<us> id=<sid>`.
 *  2. A viewer clicking "watch" arrives as
 *     `notifyjoinstreamrequest clid=<viewer> id=<sid> is_remove=0`.
 *  3. We reply `respondjoinstreamrequest id clid decision=1 offer=<sdp>`. One
 *     PeerConnection per viewer, all fed by the same capture track.
 *  4. The viewer answers with `notifystreamsignaling json={"cmd":"answer",
 *     "args":{"answer":<sdp>}}`; ICE trickles both ways as `iceCandidate`.
 *  5. `is_remove=1` / `notifystreamclientleft` drops that viewer; `stopstream
 *     id=<sid>` ends the share.
 *
 * The offer is shaped like a real TeamSpeak streamer's, because the desktop
 * viewer is fragile about what it is sent:
 *  - **Never offer H264 Constrained Baseline (42e01f).** TS6 6.0 accepts it
 *    in its answer, has no decoder for it, and the whole client crashes. Only
 *    H264 High and VP8, the formats TS streamers themselves send, are offered.
 *  - The track's stream id is `outgoing_video`, as TS streamers label theirs.
 *  - No candidates inline; they are trickled, the way TS streamers send them.
 *  - An audio m-line follows video, matching the desktop offer shape: stereo
 *    Opus carrying the phone's own playback when the share has sound
 *    (PHA-4176, [ShareAudioCapture]), inactive otherwise.
 *
 * Capture needs a MediaProjection grant ([permissionData], from the system
 * "Start recording or casting?" prompt) and, on Android 14+, a running
 * foreground service of type `mediaProjection` *before* capture starts. The
 * owning service handles that.
 *
 * Threading: the owner calls in on the main thread, and every WebRTC /
 * MediaProjection callback is posted back onto it ([onMain]), so the viewer
 * map, stream id and return codes are only ever touched from main.
 */
class StreamSenderSession(
    private val context: Context,
    private val eglBase: EglBase,
    private val permissionData: Intent,
    /** Share the phone's playback audio too (Settings, on by default). */
    private val withAudio: Boolean,
    private val ownClientId: () -> Long?,
    private val sendRaw: (name: String, args: Map<String, String>) -> Unit,
    private val onState: (StreamSendState) -> Unit,
) {
    private val tag = "StreamSender"
    private val main = Handler(Looper.getMainLooper())
    private val factory: PeerConnectionFactory
    private var capturer: ScreenCapturerAndroid? = null
    private var textureHelper: SurfaceTextureHelper? = null
    private var source: VideoSource? = null
    private var track: VideoTrack? = null
    private var audioSource: org.webrtc.AudioSource? = null
    private var audioTrack: org.webrtc.AudioTrack? = null
    @Volatile private var audioCapture: ShareAudioCapture? = null
    // PHA-4176: the encoder ceiling per viewer, from the share's quality settings.
    private var maxFps = 30
    private var maxBitrateKbps = 4608
    // The capture size in use, so a display change that keeps it is a no-op.
    private var captureWidth = 0
    private var captureHeight = 0
    private val viewers = HashMap<Long, PeerConnection>()
    private var sid: String = ""
    private var nextReturnCode = 85000
    private val ourReturnCodes = HashSet<String>()
    // The `setupstream` return code, and whether the server refused it. Both
    // matter after stop(): see [awaitingStreamId].
    private var setupReturnCode: String? = null
    private var setupRefused = false
    @Volatile private var state = StreamSendState()
    @Volatile private var closed = false

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context.applicationContext)
                .setFieldTrials("")
                .createInitializationOptions(),
        )
        val adm = JavaAudioDeviceModule.builder(context.applicationContext)
            // The module's own recording is only a 10 ms clock and a buffer:
            // [ShareAudioCapture] overwrites every buffer with the phone's
            // playback (or silence), so no mic audio ever reaches the share.
            // The voice call keeps the mic through AudioEngine.
            .setUseHardwareAcousticEchoCanceler(false)
            .setUseHardwareNoiseSuppressor(false)
            .setAudioSource(MediaRecorder.AudioSource.MIC)
            .setUseStereoInput(true)
            .setAudioBufferCallback { buffer, format, channels, rate, bytes, captureTimeNs ->
                try {
                    val cap = audioCapture
                    if (cap != null) {
                        cap.fill(buffer, format, channels, rate, bytes)
                    } else {
                        val dst = buffer.duplicate()
                        dst.clear()
                        dst.put(ByteArray(minOf(bytes, dst.capacity())))
                    }
                } catch (t: Throwable) {
                    // WebRTC's record thread: a throw here would take the app down.
                    Log.w(tag, "share audio buffer failed", t)
                }
                captureTimeNs
            }
            .setAudioRecordErrorCallback(object : JavaAudioDeviceModule.AudioRecordErrorCallback {
                override fun onWebRtcAudioRecordInitError(msg: String?) = trace("audio module init error: $msg")
                override fun onWebRtcAudioRecordStartError(code: JavaAudioDeviceModule.AudioRecordStartErrorCode?, msg: String?) =
                    trace("audio module start error $code: $msg")
                override fun onWebRtcAudioRecordError(msg: String?) = trace("audio module error: $msg")
            })
            .createAudioDeviceModule()
        factory = PeerConnectionFactory.builder()
            .setAudioDeviceModule(adm)
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            // enableH264HighProfile = true: High is the only H264 a TS viewer survives.
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .createPeerConnectionFactory()
        adm.release()
    }

    val current: StreamSendState get() = state

    /**
     * `setupstream` went out and the server has neither named the stream nor
     * refused it. If that is still true after [stop], the server will start a
     * stream nobody is sending; the owner keeps feeding [onLateRaw] until it
     * arrives so it can be stopped at once.
     */
    val awaitingStreamId: Boolean get() = setupReturnCode != null && sid.isEmpty() && !setupRefused

    /** Start capturing and announce the share as [title]. */
    fun start(title: String, width: Int, height: Int, fps: Int = 30, bitrateKbps: Int = 4608) {
        check(capturer == null) { "already sharing" }
        maxFps = fps
        maxBitrateKbps = bitrateKbps
        update(StreamSendState(phase = StreamSendPhase.STARTING, title = title))
        val helper = SurfaceTextureHelper.create("ScreenShare", eglBase.eglBaseContext)
        val src = factory.createVideoSource(/* isScreencast = */ true)
        val cap = ScreenCapturerAndroid(
            permissionData,
            object : MediaProjection.Callback() {
                override fun onStop() {
                    // The user revoked it from the system UI / status bar chip.
                    // Arrives on the capturer's thread, whose helper stop()
                    // disposes; hop to main, and never let it throw there.
                    main.post {
                        try {
                            stop("screen capture stopped")
                        } catch (t: Throwable) {
                            Log.w(tag, "stop after projection end failed", t)
                        }
                    }
                }
            },
        )
        runCatching {
            cap.initialize(helper, context.applicationContext, src.capturerObserver)
            cap.startCapture(width, height, fps)
        }.onFailure {
            Log.e(tag, "capture start failed", it)
            update(state.copy(phase = StreamSendPhase.FAILED, detail = "could not capture the screen: ${it.message}"))
            runCatching { cap.dispose() }
            src.dispose()
            helper.dispose()
            // Nothing else holds the factory now; a later stop() is a no-op.
            closed = true
            runCatching { factory.dispose() }
            return
        }
        textureHelper = helper
        source = src
        capturer = cap
        captureWidth = width
        captureHeight = height
        track = factory.createVideoTrack("outgoing_video_track", src)
        startAudio(cap)
        trace("setupstream ${width}x$height ${fps}fps ${bitrateKbps}kbps audio=${audioTrack != null}")
        setupReturnCode = sendCommand(
            "setupstream",
            mapOf(
                "name" to title,
                "type" to "3",
                "accessibility" to "1",
                "mode" to "1",
                "bitrate" to bitrateKbps.toString(),
                "viewer_limit" to "0",
                "audio" to if (audioTrack != null) "1" else "0",
            ),
        )
    }

    /**
     * Adds the phone's playback to the share. Needs Android 10 (playback
     * capture) and the mic permission (Android gates playback capture on
     * RECORD_AUDIO); without either the share goes out picture-only, which is
     * what the server is then told, so the desktop shows no audio control.
     */
    private fun startAudio(cap: ScreenCapturerAndroid) {
        if (!withAudio) return
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            trace("no share audio: Android ${Build.VERSION.SDK_INT} has no playback capture")
            return
        }
        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            trace("no share audio: microphone permission not granted")
            return
        }
        val projection = cap.mediaProjection ?: run {
            trace("no share audio: capturer has no MediaProjection")
            return
        }
        runCatching {
            audioCapture = ShareAudioCapture(projection) { trace(it) }
            // No voice processing: this is music and game audio, and echo
            // cancellation / noise suppression / AGC would chew it up.
            val constraints = MediaConstraints().apply {
                listOf("googEchoCancellation", "googAutoGainControl", "googNoiseSuppression", "googHighpassFilter")
                    .forEach { mandatory.add(MediaConstraints.KeyValuePair(it, "false")) }
            }
            val asrc = factory.createAudioSource(constraints)
            audioSource = asrc
            audioTrack = factory.createAudioTrack("outgoing_audio_track", asrc)
        }.onFailure {
            trace("no share audio: $it")
            releaseAudio()
        }
    }

    private fun releaseAudio() {
        runCatching { audioTrack?.dispose() }
        audioTrack = null
        runCatching { audioSource?.dispose() }
        audioSource = null
        audioCapture?.release()
        audioCapture = null
    }

    /**
     * Follow a rotation: re-shape the capture to [width] x [height]. The
     * capturer resizes its virtual display in place (one MediaProjection can
     * only ever create one on Android 14+), and the encoder carries the new
     * size to every viewer on its next frame, so no renegotiation is needed.
     */
    fun resize(width: Int, height: Int) {
        val cap = capturer ?: return
        if (closed || (width == captureWidth && height == captureHeight)) return
        runCatching { cap.changeCaptureFormat(width, height, maxFps) }
            .onSuccess {
                captureWidth = width
                captureHeight = height
                trace("capture resized ${width}x$height")
            }
            .onFailure { trace("capture resize to ${width}x$height failed: $it") }
    }

    /** End the share and release everything. Idempotent. */
    fun stop(reason: String = "stopped") {
        if (closed) return
        closed = true
        trace("stop: $reason (viewers=${viewers.size})")
        if (sid.isNotEmpty()) runCatching { sendCommand("stopstream", mapOf("id" to sid)) }
        // close() then dispose(): close alone leaves the native peer (and its
        // threads) alive until the factory goes, and disposing the factory
        // under a live peer is undefined.
        viewers.values.forEach { runCatching { it.close() }; runCatching { it.dispose() } }
        viewers.clear()
        runCatching { capturer?.stopCapture() }
        runCatching { capturer?.dispose() }
        capturer = null
        runCatching { track?.dispose() }
        track = null
        releaseAudio()
        runCatching { source?.dispose() }
        source = null
        runCatching { textureHelper?.dispose() }
        textureHelper = null
        runCatching { factory.dispose() }
        if (state.phase != StreamSendPhase.FAILED) {
            update(state.copy(phase = StreamSendPhase.ENDED, viewers = 0, detail = reason))
        }
    }

    /** Feed every [CoreEvent.RawCommand]; the session picks out its own. */
    fun onRaw(ev: CoreEvent.RawCommand) {
        if (closed) return
        val a = ev.args
        when (ev.name) {
            "notifystreamstarted" -> {
                val own = ownClientId() ?: return
                if (a["clid"] != own.toString() || sid.isNotEmpty()) return
                sid = a["id"] ?: return
                trace("live id=$sid")
                update(state.copy(phase = StreamSendPhase.LIVE, streamId = sid))
            }
            "notifyjoinstreamrequest" -> {
                if (sid.isEmpty() || a["id"] != sid) return
                val clid = a["clid"]?.toLongOrNull() ?: return
                trace("viewer $clid ${if (a["is_remove"] == "1") "leaves" else "asks to join"}")
                if (a["is_remove"] == "1") dropViewer(clid) else admitViewer(clid)
            }
            "notifystreamclientleft", "notifyremovedfromstream" -> {
                if (a["id"] == sid) a["clid"]?.toLongOrNull()?.let {
                    trace("${ev.name} viewer $it")
                    dropViewer(it)
                }
            }
            "notifystreamsignaling" -> {
                if (a["id"] != sid) return
                val clid = a["clid"]?.toLongOrNull() ?: return
                val json = runCatching { JSONObject(a["json"] ?: return) }.getOrNull() ?: return
                val peer = viewers[clid] ?: run {
                    trace("signalling ${json.optString("cmd")} from $clid, who has no peer here")
                    return
                }
                val args = json.optJSONObject("args") ?: JSONObject()
                when (json.optString("cmd")) {
                    "answer" -> {
                        trace("answer from viewer $clid")
                        val sdp = args.optString("answer").ifEmpty { args.optString("sdp") }
                        peer.setRemoteDescription(
                            sdpObserver("setRemote(answer) clid=$clid") {},
                            SessionDescription(SessionDescription.Type.ANSWER, sdp),
                        )
                    }
                    "iceCandidate" ->
                        peer.addIceCandidate(IceCandidate(args.optString("mid"), args.optInt("mLine", 0), args.optString("sdp")))
                    else -> trace("unhandled signalling cmd=${json.optString("cmd")} from $clid")
                }
            }
            "error" -> {
                val rc = a["return_code"] ?: return
                if (!ourReturnCodes.remove(rc)) return
                if (a["id"] != "0") trace("server error ${a["id"]} ${a["msg"]} (rc=$rc)")
                if (rc == setupReturnCode && a["id"] != "0") setupRefused = true
                if (a["id"] != "0" && sid.isEmpty()) {
                    // setupstream itself was refused (permissions, server without streaming).
                    update(state.copy(phase = StreamSendPhase.FAILED, detail = a["msg"] ?: "error ${a["id"]}"))
                }
            }
        }
    }

    /**
     * Only after [stop], while [awaitingStreamId]: the server's late answer to
     * our `setupstream`. A `notifystreamstarted` for us gets an immediate
     * `stopstream`; a refusal settles it. Returns whether [ev] was ours, in
     * which case it must not also go to a newer session.
     */
    fun onLateRaw(ev: CoreEvent.RawCommand): Boolean {
        if (!closed || !awaitingStreamId) return false
        val a = ev.args
        when (ev.name) {
            "notifystreamstarted" -> {
                val own = ownClientId() ?: return false
                if (a["clid"] != own.toString()) return false
                sid = a["id"] ?: return false
                Log.i(tag, "late notifystreamstarted for a stopped share — stopping stream $sid")
                runCatching { sendCommand("stopstream", mapOf("id" to sid)) }
                return true
            }
            "error" -> {
                if (a["return_code"] != setupReturnCode) return false
                if (a["id"] != "0") setupRefused = true
                return true
            }
        }
        return false
    }

    // ---- per-viewer WebRTC ----------------------------------------------

    private fun admitViewer(clid: Long) {
        if (viewers.containsKey(clid)) return
        val t = track ?: return
        val config = PeerConnection.RTCConfiguration(
            listOf(
                PeerConnection.IceServer.builder("stun:turn.teamspeak.com:3478").createIceServer(),
                PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
            ),
        ).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        }
        val peer = factory.createPeerConnection(config, observerFor(clid)) ?: return
        viewers[clid] = peer
        val video = peer.addTransceiver(
            t,
            RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.SEND_ONLY, listOf("outgoing_video")),
        )
        capVideo(clid, video.sender)
        val audio = audioTrack
        if (audio != null) {
            peer.addTransceiver(
                audio,
                RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.SEND_ONLY, listOf("outgoing_video")),
            )
        } else {
            peer.addTransceiver(
                MediaStreamTrack.MediaType.MEDIA_TYPE_AUDIO,
                RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.INACTIVE),
            )
        }
        update(state.copy(viewers = viewers.size))
        peer.createOffer(
            object : SdpObserver by noopSdp {
                override fun onCreateSuccess(desc: SessionDescription) = onMain {
                    // Dropped (and disposed) while the offer was being made.
                    if (viewers[clid] !== peer) return@onMain
                    val shaped = TsOfferShape.apply(desc.description)
                        ?.let { if (audio != null) TsOfferShape.withStereoOpus(it) else it }
                    if (shaped == null) {
                        Log.w(tag, "no TeamSpeak-safe video codec on this device")
                        update(state.copy(phase = StreamSendPhase.FAILED, detail = "this phone has no video encoder TeamSpeak can play"))
                        dropViewer(clid, peer)
                        return@onMain
                    }
                    val local = SessionDescription(SessionDescription.Type.OFFER, shaped)
                    peer.setLocalDescription(
                        sdpObserver("setLocal(offer) clid=$clid") {
                            if (viewers[clid] !== peer) return@sdpObserver
                            trace("offer to viewer $clid")
                            sendCommand(
                                "respondjoinstreamrequest",
                                mapOf(
                                    "id" to sid,
                                    "clid" to clid.toString(),
                                    "decision" to "1",
                                    "offer" to TsOfferShape.withoutCandidates(shaped),
                                    "msg" to "",
                                ),
                            )
                        },
                        local,
                    )
                }
                override fun onCreateFailure(error: String?) = onMain {
                    trace("createOffer for $clid failed: $error")
                    dropViewer(clid, peer)
                }
            },
            MediaConstraints(),
        )
    }

    /**
     * Holds the encoder to the share's bitrate and frame rate. Without this
     * libwebrtc picks its own ceiling, which is what made the Settings choice
     * (and Automatic's "max on Wi-Fi") meaningless.
     */
    private fun capVideo(clid: Long, sender: org.webrtc.RtpSender) {
        runCatching {
            val p = sender.parameters
            p.encodings.forEach {
                it.maxBitrateBps = maxBitrateKbps * 1000
                it.maxFramerate = maxFps
            }
            check(sender.setParameters(p)) { "setParameters refused" }
        }.onFailure { trace("video limits for viewer $clid not applied: $it") }
    }

    /** Drops [clid]'s peer — only if it is still [expected], when given. */
    private fun dropViewer(clid: Long, expected: PeerConnection? = null) {
        if (expected != null && viewers[clid] !== expected) return
        viewers.remove(clid)?.let { runCatching { it.close() }; runCatching { it.dispose() } }
        update(state.copy(viewers = viewers.size))
    }

    // WebRTC calls these on its signalling thread; each hops to main.
    private fun observerFor(clid: Long) = object : PeerConnection.Observer {
        override fun onIceCandidate(c: IceCandidate) = onMain {
            if (!viewers.containsKey(clid)) return@onMain
            sendCommand(
                "streamsignaling",
                mapOf(
                    "id" to sid,
                    "clid" to clid.toString(),
                    "json" to JSONObject().put("cmd", "iceCandidate").put(
                        "args",
                        JSONObject().put("mLine", c.sdpMLineIndex).put("mid", c.sdpMid ?: c.sdpMLineIndex.toString()).put("sdp", c.sdp),
                    ).toString(),
                ),
            )
        }
        override fun onIceConnectionChange(s: PeerConnection.IceConnectionState?) {
            trace("viewer $clid ice=$s")
            if (s == PeerConnection.IceConnectionState.FAILED) onMain { dropViewer(clid) }
        }
        override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {}
        override fun onSignalingChange(s: PeerConnection.SignalingState?) {}
        override fun onIceConnectionReceivingChange(b: Boolean) {}
        override fun onIceGatheringChange(s: PeerConnection.IceGatheringState?) {}
        override fun onAddStream(stream: MediaStream?) {}
        override fun onRemoveStream(stream: MediaStream?) {}
        override fun onDataChannel(dc: org.webrtc.DataChannel?) {}
        override fun onRenegotiationNeeded() {}
        override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) {}
    }

    /**
     * PHA-4176: every step of a share goes into the uploaded diagnostic trace.
     * "Waiting to be let in…" on a desktop has to be placed on one step —
     * join request never seen, offer never sent, answer never back, ICE —
     * without anyone attaching logcat.
     */
    private fun trace(line: String) {
        Log.i(tag, line)
        Diag.add("share: $line")
    }

    /** Sends [name] tagged with a fresh return code, which it returns. */
    private fun sendCommand(name: String, args: Map<String, String>): String {
        val rc = (nextReturnCode++).toString()
        ourReturnCodes.add(rc)
        sendRaw(name, args + ("return_code" to rc))
        return rc
    }

    /** Runs [block] on the main thread, unless the session has been stopped by then. */
    private fun onMain(block: () -> Unit) {
        main.post { if (!closed) block() }
    }

    private fun update(s: StreamSendState) {
        state = s
        onState(s)
    }

    private val noopSdp = object : SdpObserver {
        override fun onCreateSuccess(p0: SessionDescription?) {}
        override fun onSetSuccess() {}
        override fun onCreateFailure(p0: String?) {}
        override fun onSetFailure(p0: String?) {}
    }

    private fun sdpObserver(what: String, onSet: () -> Unit) = object : SdpObserver by noopSdp {
        override fun onSetSuccess() = onMain(onSet)
        override fun onSetFailure(error: String?) {
            trace("$what failed: $error")
        }
    }
}
