package com.plnt.client.stream

import android.content.Context
import android.util.Log
import com.plnt.client.core.CoreEvent
import org.json.JSONObject
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.RtpTransceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.VideoFrame
import org.webrtc.VideoSink
import org.webrtc.VideoTrack
import org.webrtc.audio.JavaAudioDeviceModule

/**
 * Watches one TS6 screen share (PHA-3289).
 *
 * The protocol, as proven against a live TS6 6.0 streamer with the RAID probe
 * (see the issue thread for the captures):
 *
 *  1. `joinstreamrequest id=<sid> clid=<streamer> msg=<nick> is_remove=0`.
 *     The streamer auto-approves and the server relays
 *     `notifyrespondjoinstreamrequest decision=1 offer=<sdp>`.
 *  2. Answer *immediately* (a real viewer replies within ~100 ms) with
 *     `streamsignaling json={"cmd":"answer","args":{"answer":<sdp>}}` — the
 *     key is `answer`, not `sdp`. libwebrtc's own createAnswer echoes the
 *     offer's codecs and extensions back the way the streamer expects; the
 *     desktop viewer is the same library, so no SDP surgery is needed.
 *  3. Trickle our candidates as
 *     `{"cmd":"iceCandidate","args":{"mLine":i,"mid":"i","sdp":"candidate:…"}}`
 *     and feed theirs (same shape, in `notifystreamsignaling`) to the peer.
 *  4. ~200 ms after our answer the streamer sends a *second* offer
 *     (`{"cmd":"offer","args":{"offer":<sdp>}}`, a codec realign). Media never
 *     starts until that one is answered too, the same way.
 *  5. Leave with `joinstreamrequest … is_remove=1`.
 *
 * Signalling rides the normal TS3-style command channel, so this works on any
 * TS6 server the voice client can already join. Media is a direct WebRTC
 * connection to the streamer's PC: the streamer's own candidates come from
 * TeamSpeak's STUN server, and we gather ours the same way. There is no relay;
 * a phone behind carrier NAT that cannot reach the streamer directly will fail
 * at [StreamViewPhase.CONNECTING]. A user-supplied TURN server is the planned
 * answer for that, not anything hosted by us.
 *
 * All callbacks arrive on WebRTC's own threads; [onState] is dispatched as-is
 * and the owner hops to Main.
 */
class StreamViewerSession(
    context: Context,
    private val eglBase: EglBase,
    private val ownNickname: String,
    private val sendRaw: (name: String, args: Map<String, String>) -> Unit,
    private val onState: (StreamViewState) -> Unit,
) {
    private val tag = "StreamViewer"
    private val factory: PeerConnectionFactory
    private var pc: PeerConnection? = null
    private var sink: VideoSink? = null
    private var videoTrack: VideoTrack? = null
    private var sid: String = ""
    private var streamerClid: Long = 0
    private var nextReturnCode = 80000
    private val ourReturnCodes = HashSet<String>()
    @Volatile private var state = StreamViewState()
    @Volatile private var closed = false

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context.applicationContext)
                .setFieldTrials("")
                .createInitializationOptions(),
        )
        val adm = JavaAudioDeviceModule.builder(context.applicationContext)
            // The voice call already owns the mic through AudioEngine; this
            // module only ever plays the stream's audio.
            .setUseHardwareAcousticEchoCanceler(false)
            .setUseHardwareNoiseSuppressor(false)
            .createAudioDeviceModule()
        factory = PeerConnectionFactory.builder()
            .setAudioDeviceModule(adm)
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .createPeerConnectionFactory()
        adm.release()
    }

    val current: StreamViewState get() = state

    /** Ask to join [stream]. Progress arrives through [onState]. */
    fun start(stream: StreamInfo) {
        check(pc == null) { "session already started" }
        sid = stream.id
        streamerClid = stream.streamerClientId
        update(StreamViewState(phase = StreamViewPhase.JOINING, stream = stream))

        val config = PeerConnection.RTCConfiguration(
            listOf(
                // The desktop client gathers against TeamSpeak's own STUN; a
                // public fallback covers a server operator who blocks it.
                PeerConnection.IceServer.builder("stun:turn.teamspeak.com:3478").createIceServer(),
                PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
            ),
        ).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        }
        pc = factory.createPeerConnection(config, observer)
            ?: run {
                update(state.copy(phase = StreamViewPhase.FAILED, detail = "WebRTC unavailable on this device"))
                return
            }
        sendCommand(
            "joinstreamrequest",
            mapOf("id" to sid, "clid" to streamerClid.toString(), "msg" to ownNickname, "is_remove" to "0"),
        )
    }

    /** Where decoded frames go. Safe to call before the track exists, and again after a rotation. */
    fun attach(videoSink: VideoSink?) {
        val old = sink
        sink = videoSink
        videoTrack?.let { t ->
            old?.let { runCatching { t.removeSink(it) } }
            videoSink?.let { runCatching { t.addSink(it) } }
        }
    }

    /** Leave the stream and release WebRTC. Idempotent. */
    fun stop(reason: String = "stopped") {
        if (closed) return
        closed = true
        if (sid.isNotEmpty() && state.phase != StreamViewPhase.ENDED && state.phase != StreamViewPhase.FAILED) {
            runCatching {
                sendCommand(
                    "joinstreamrequest",
                    mapOf("id" to sid, "clid" to streamerClid.toString(), "msg" to ownNickname, "is_remove" to "1"),
                )
            }
        }
        sink?.let { s -> videoTrack?.let { runCatching { it.removeSink(s) } } }
        videoTrack = null
        runCatching { pc?.close() }
        pc = null
        runCatching { factory.dispose() }
        if (state.phase != StreamViewPhase.ENDED && state.phase != StreamViewPhase.FAILED) {
            update(state.copy(phase = StreamViewPhase.ENDED, detail = reason))
        }
    }

    /** Feed every [CoreEvent.RawCommand]; the session picks out its own. */
    fun onRaw(ev: CoreEvent.RawCommand) {
        if (closed || sid.isEmpty()) return
        val a = ev.args
        when (ev.name) {
            "notifyrespondjoinstreamrequest" -> {
                if (a["id"] != sid) return
                if (a["decision"] != "1") {
                    update(state.copy(phase = StreamViewPhase.FAILED, detail = "streamer declined"))
                    return
                }
                val offer = a["offer"] ?: return
                update(state.copy(phase = StreamViewPhase.CONNECTING))
                applyOffer(offer)
            }
            "notifystreamsignaling" -> {
                if (a["id"] != sid) return
                val json = runCatching { JSONObject(a["json"] ?: return) }.getOrNull() ?: return
                val args = json.optJSONObject("args") ?: JSONObject()
                when (json.optString("cmd")) {
                    "iceCandidate" -> {
                        val cand = IceCandidate(args.optString("mid"), args.optInt("mLine", 0), args.optString("sdp"))
                        pc?.addIceCandidate(cand)
                    }
                    // The post-answer realign: answer it like the first one.
                    "offer" -> args.optString("offer").takeIf { it.isNotEmpty() }?.let(::applyOffer)
                    else -> Log.i(tag, "unhandled signalling cmd=${json.optString("cmd")}")
                }
            }
            "notifystreamclientleft", "notifyremovedfromstream" -> {
                if (a["id"] == sid) {
                    // Our own leave echoes back as well; stop() already set ENDED then.
                    if (!closed) update(state.copy(phase = StreamViewPhase.ENDED, detail = "removed from stream"))
                }
            }
            "notifystreamstopped", "notifystreamended" -> {
                if (a["id"] == sid) update(state.copy(phase = StreamViewPhase.ENDED, detail = "stream ended"))
            }
            "error" -> {
                val rc = a["return_code"] ?: return
                if (!ourReturnCodes.remove(rc)) return
                if (a["id"] != "0") {
                    update(state.copy(phase = StreamViewPhase.FAILED, detail = a["msg"] ?: "error ${a["id"]}"))
                }
            }
        }
    }

    // ---- WebRTC plumbing -------------------------------------------------

    private fun applyOffer(sdp: String) {
        val peer = pc ?: return
        peer.setRemoteDescription(
            sdpObserver("setRemote(offer)") {
                peer.createAnswer(
                    object : SdpObserver by noopSdp {
                        override fun onCreateSuccess(desc: SessionDescription) {
                            peer.setLocalDescription(
                                sdpObserver("setLocal(answer)") {
                                    // Non-trickle candidates are not needed: the
                                    // answer goes out at once and ICE trickles.
                                    sendSignal(JSONObject().put("cmd", "answer").put("args", JSONObject().put("answer", desc.description)))
                                },
                                desc,
                            )
                        }
                        override fun onCreateFailure(error: String?) {
                            update(state.copy(phase = StreamViewPhase.FAILED, detail = "createAnswer: $error"))
                        }
                    },
                    MediaConstraints(),
                )
            },
            SessionDescription(SessionDescription.Type.OFFER, sdp),
        )
    }

    private fun sendSignal(json: JSONObject) {
        sendCommand("streamsignaling", mapOf("id" to sid, "clid" to streamerClid.toString(), "json" to json.toString()))
    }

    private fun sendCommand(name: String, args: Map<String, String>) {
        val rc = (nextReturnCode++).toString()
        ourReturnCodes.add(rc)
        sendRaw(name, args + ("return_code" to rc))
    }

    private val observer = object : PeerConnection.Observer {
        override fun onIceCandidate(c: IceCandidate) {
            sendSignal(
                JSONObject().put("cmd", "iceCandidate").put(
                    "args",
                    JSONObject().put("mLine", c.sdpMLineIndex).put("mid", c.sdpMid ?: c.sdpMLineIndex.toString()).put("sdp", c.sdp),
                ),
            )
        }
        override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {}
        override fun onSignalingChange(s: PeerConnection.SignalingState?) {}
        override fun onIceConnectionChange(s: PeerConnection.IceConnectionState?) {
            Log.i(tag, "ice=$s")
            when (s) {
                PeerConnection.IceConnectionState.FAILED ->
                    update(state.copy(phase = StreamViewPhase.FAILED, detail = "could not reach the streamer (no direct route)"))
                PeerConnection.IceConnectionState.DISCONNECTED ->
                    if (state.phase == StreamViewPhase.PLAYING) update(state.copy(phase = StreamViewPhase.CONNECTING))
                else -> {}
            }
        }
        override fun onIceConnectionReceivingChange(b: Boolean) {}
        override fun onIceGatheringChange(s: PeerConnection.IceGatheringState?) {}
        override fun onAddStream(stream: MediaStream?) {}
        override fun onRemoveStream(stream: MediaStream?) {}
        override fun onDataChannel(dc: org.webrtc.DataChannel?) {}
        override fun onRenegotiationNeeded() {}
        override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) {}
        override fun onTrack(transceiver: RtpTransceiver?) {
            val track = transceiver?.receiver?.track() as? VideoTrack ?: return
            videoTrack = track
            track.addSink(frameProbe)
            sink?.let { runCatching { track.addSink(it) } }
        }
    }

    /** Flips to PLAYING on the first decoded frame and records its size. */
    private val frameProbe = object : VideoSink {
        override fun onFrame(frame: VideoFrame) {
            val w = frame.rotatedWidth
            val h = frame.rotatedHeight
            val s = state
            if (s.phase != StreamViewPhase.PLAYING || s.width != w || s.height != h) {
                update(s.copy(phase = StreamViewPhase.PLAYING, width = w, height = h, detail = null))
            }
        }
    }

    private fun update(s: StreamViewState) {
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
        override fun onSetSuccess() = onSet()
        override fun onSetFailure(error: String?) {
            Log.w(tag, "$what failed: $error")
            update(state.copy(phase = StreamViewPhase.FAILED, detail = "$what: $error"))
        }
    }
}
