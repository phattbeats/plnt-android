package com.plnt.client.stream

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IceServerConfigTest {
    @Test
    fun noTurnConfiguredMeansStunOnly() {
        assertEquals(IceServerConfig.STUN, IceServerConfig.build("", "", ""))
    }

    @Test
    fun aValidTurnUrlIsAddedAfterStunWithCredentials() {
        val got = IceServerConfig.build(" turn:relay.example.org:3478?transport=udp ", " bob ", "s3cret")
        assertEquals(IceServerConfig.STUN + IceServerSpec("turn:relay.example.org:3478?transport=udp", "bob", "s3cret"), got)
    }

    @Test
    fun turnsSchemeIsAccepted() {
        assertTrue(IceServerConfig.isValidTurnUrl("turns:relay.example.org:5349"))
    }

    @Test
    fun garbageIsIgnoredNotPassedToWebRtc() {
        listOf("relay.example.org", "http://x", "turn:", "turn:?transport=udp", "turn:a b").forEach {
            assertFalse(it, IceServerConfig.isValidTurnUrl(it))
            assertEquals(IceServerConfig.STUN, IceServerConfig.build(it, "u", "p"))
        }
    }
}
