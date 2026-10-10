package com.plnt.client.model

import java.util.concurrent.atomic.AtomicLong

/** Rechecked at dispatch, not merely when a native callback enters the queue. */
class ConnectionGeneration {
    private val value = AtomicLong(0)
    fun current(): Long = value.get()
    fun invalidate(): Long = value.incrementAndGet()
    fun accepts(token: Long): Boolean = current() == token
    fun accepts(token: Long, expectedOwner: Any?, actualOwner: Any?): Boolean =
        accepts(token) && expectedOwner != null && actualOwner === expectedOwner
}

/** No identity/password in UI replay; the service remains the connection owner. */
data class VoiceConnectionSnapshot(
    val generation: Long = 0,
    val status: ServerConnectionStatus = ServerConnectionStatus(),
    val ownClientId: Long? = null,
    val serverName: String = "",
    val lastError: String? = null,
)

enum class VoiceBindingPhase { BINDING, BOUND, FAILED }

data class VoiceServiceBindingStatus(
    val phase: VoiceBindingPhase = VoiceBindingPhase.BINDING,
    val error: String? = null,
) {
    val canQueue: Boolean get() = phase == VoiceBindingPhase.BINDING
    val failed: Boolean get() = phase == VoiceBindingPhase.FAILED
    fun bound() = VoiceServiceBindingStatus(VoiceBindingPhase.BOUND)
    fun failure(reason: String) = VoiceServiceBindingStatus(VoiceBindingPhase.FAILED, reason)
}
