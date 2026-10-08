package com.plnt.client.stream

/**
 * One TS6 screen share (or camera stream) the server has told us about
 * (PHA-3289). Built from `notifystreamstarted` / `notifystreaminfo` /
 * `notifystreamupdated`, whose argument names are the only schema we have:
 * `id`, `clid`, `name`, `type`, `audio`, `viewer`, `bitrate`, `accessibility`.
 */
data class StreamInfo(
    /** Server-side stream session id (a UUID string). */
    val id: String,
    /** The client that is streaming. */
    val streamerClientId: Long,
    /** Window / display title the streamer picked, as the server relays it. */
    val name: String,
    /** `type` as sent by the server; 3 has only ever meant "screen" in captures. */
    val type: Int,
    val hasAudio: Boolean,
    val viewerCount: Int,
)

/** Where a viewing session is, for the viewer screen's status line. */
enum class StreamViewPhase { IDLE, JOINING, CONNECTING, PLAYING, ENDED, FAILED }

data class StreamViewState(
    val phase: StreamViewPhase = StreamViewPhase.IDLE,
    val stream: StreamInfo? = null,
    /** Human-readable detail for ENDED / FAILED, null otherwise. */
    val detail: String? = null,
    val width: Int = 0,
    val height: Int = 0,
)
