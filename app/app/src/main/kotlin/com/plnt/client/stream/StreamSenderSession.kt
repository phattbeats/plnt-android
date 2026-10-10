package com.plnt.client.stream

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.util.Log
import com.plnt.client.core.CoreEvent
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
 *  - An inactive audio m-line follows video, matching the desktop offer shape.
 *
 * Capture needs a MediaProjection grant ([permissionData], from the system
 * "Start recording or casting?" prompt) and, on Android 14+, a running
 * foreground service of type `mediaProjection` *before* capture starts. The
 * owning service handles that.
 */
class StreamSenderSession(
    private val context: Context,
    private val eglBase: EglBase,
    private val permissionData: Intent,
    private val ownClientId: () -> Long?,
    private val sendRaw: (name: String, args: Map<String, String>) -> Unit,
    private val onState: (StreamSendState) -> Unit,
) {
    private val tag = "StreamSender"
    private val factory: PeerConnectionFactory
    private var capturer: ScreenCapturerAndroid? = null
    private var textureHelper: SurfaceTextureHelper? = null
    private var source: VideoSource? = null
    private var track: VideoTrack? = null
    private val viewers = HashMap<Long, PeerConnection>()
    private var sid: String = ""
    private var nextReturnCode = 85000
    private val ourReturnCodes = HashSet<String>()
    @Volatile private var state = StreamSendState()
    @Volatile private var closed = false

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context.applicationContext)
                .setFieldTrials("")
                .createInitializationOptions(),
        )
        val adm = JavaAudioDeviceModule.builder(context.applicationContext)
            // Video-only share; the voice call keeps the mic through AudioEngine.
            .setUseHardwareAcousticEchoCanceler(false)
            .setUseHardwareNoiseSuppressor(false)
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

    /** Start capturing and announce the share as [title]. */
    fun start(title: String, width: Int, height: Int, fps: Int = 30) {
        check(capturer == null) { "already sharing" }
        update(StreamSendState(phase = StreamSendPhase.STARTING, title = title))
        val helper = SurfaceTextureHelper.create("ScreenShare", eglBase.eglBaseContext)
        val src = factory.createVideoSource(/* isScreencast = */ true)
        val cap = ScreenCapturerAndroid(
            permissionData,
            object : MediaProjection.Callback() {
                override fun onStop() {
                    // The user revoked it from the system UI / status bar chip.
                    stop("screen capture stopped")
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
            return
        }
        textureHelper = helper
        source = src
        capturer = cap
        track = factory.createVideoTrack("outgoing_video_track", src)
        sendCommand(
            "setupstream",
            mapOf(
                "name" to title,
                "type" to "3",
                "accessibility" to "1",
                "mode" to "1",
                "bitrate" to "4608",
                "viewer_limit" to "0",
                "audio" to "0",
            ),
        )
    }

    /** End the share and release everything. Idempotent. */
    fun stop(reason: String = "stopped") {
        if (closed) return
        closed = true
        if (sid.isNotEmpty()) runCatching { sendCommand("stopstream", mapOf("id" to sid)) }
        viewers.values.forEach { runCatching { it.close() } }
        viewers.clear()
        runCatching { capturer?.stopCapture() }
        runCatching { capturer?.dispose() }
        capturer = null
        runCatching { track?.dispose() }
        track = null
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
                update(state.copy(phase = StreamSendPhase.LIVE, streamId = sid))
            }
            "notifyjoinstreamrequest" -> {
                if (sid.isEmpty() || a["id"] != sid) return
                val clid = a["clid"]?.toLongOrNull() ?: return
                if (a["is_remove"] == "1") dropViewer(clid) else admitViewer(clid)
            }
            "notifystreamclientleft", "notifyremovedfromstream" -> {
                if (a["id"] == sid) a["clid"]?.toLongOrNull()?.let(::dropViewer)
            }
            "notifystreamsignaling" -> {
                if (a["id"] != sid) return
                val clid = a["clid"]?.toLongOrNull() ?: return
                val peer = viewers[clid] ?: return
                val json = runCatching { JSONObject(a["json"] ?: return) }.getOrNull() ?: return
                val args = json.optJSONObject("args") ?: JSONObject()
                when (json.optString("cmd")) {
                    "answer" -> {
                        val sdp = args.optString("answer").ifEmpty { args.optString("sdp") }
                        peer.setRemoteDescription(
                            sdpObserver("setRemote(answer) clid=$clid") {},
                            SessionDescription(SessionDescription.Type.ANSWER, sdp),
                        )
                    }
                    "iceCandidate" ->
                        peer.addIceCandidate(IceCandidate(args.optString("mid"), args.optInt("mLine", 0), args.optString("sdp")))
                    else -> Log.i(tag, "unhandled signalling cmd=${json.optString("cmd")} from $clid")
                }
            }
            "error" -> {
                val rc = a["return_code"] ?: return
                if (!ourReturnCodes.remove(rc)) return
                if (a["id"] != "0" && sid.isEmpty()) {
                    // setupstream itself was refused (permissions, server without streaming).
                    update(state.copy(phase = StreamSendPhase.FAILED, detail = a["msg"] ?: "error ${a["id"]}"))
                }
            }
        }
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
        peer.addTransceiver(
            t,
            RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.SEND_ONLY, listOf("outgoing_video")),
        )
        peer.addTransceiver(
            MediaStreamTrack.MediaType.MEDIA_TYPE_AUDIO,
            RtpTransceiver.RtpTransceiverInit(RtpTransceiver.RtpTransceiverDirection.INACTIVE),
        )
        update(state.copy(viewers = viewers.size))
        peer.createOffer(
            object : SdpObserver by noopSdp {
                override fun onCreateSuccess(desc: SessionDescription) {
                    val shaped = TsOfferShape.apply(desc.description)
                    if (shaped == null) {
                        Log.w(tag, "no TeamSpeak-safe video codec on this device")
                        update(state.copy(phase = StreamSendPhase.FAILED, detail = "this phone has no video encoder TeamSpeak can play"))
                        dropViewer(clid)
                        return
                    }
                    val local = SessionDescription(SessionDescription.Type.OFFER, shaped)
                    peer.setLocalDescription(
                        sdpObserver("setLocal(offer) clid=$clid") {
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
                override fun onCreateFailure(error: String?) {
                    Log.w(tag, "createOffer for $clid failed: $error")
                    dropViewer(clid)
                }
            },
            MediaConstraints(),
        )
    }

    private fun dropViewer(clid: Long) {
        viewers.remove(clid)?.let { runCatching { it.close() } }
        update(state.copy(viewers = viewers.size))
    }

    private fun observerFor(clid: Long) = object : PeerConnection.Observer {
        override fun onIceCandidate(c: IceCandidate) {
            if (!viewers.containsKey(clid)) return
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
            Log.i(tag, "viewer $clid ice=$s")
            if (s == PeerConnection.IceConnectionState.FAILED) dropViewer(clid)
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

    private fun sendCommand(name: String, args: Map<String, String>) {
        val rc = (nextReturnCode++).toString()
        ourReturnCodes.add(rc)
        sendRaw(name, args + ("return_code" to rc))
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
        override fun onSetSuccess() = onSet()
        override fun onSetFailure(error: String?) {
            Log.w(tag, "$what failed: $error")
        }
    }
}
