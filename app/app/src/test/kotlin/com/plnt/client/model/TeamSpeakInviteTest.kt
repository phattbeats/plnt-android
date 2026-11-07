package com.plnt.client.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * PHA-4849: TeamSpeak's invite service and the answer it gives.
 *
 * The service is TeamSpeak's own — the same one the desktop client asks when
 * someone opens an invite link — so this parses its real shape: a flat JSON
 * object with the server's address, its name, and sometimes the channel the
 * invite points at.
 */
class TeamSpeakInviteTest {
    private val now = 1_700_000_000L

    @Test
    fun parsesAServerInvite() {
        val invite = TeamSpeakInviteService.parse(
            """{"type":"server","address":"ts.example.com","name":"Example","expires":${now + 3600}}""",
            now,
        )!!
        assertEquals("ts.example.com", invite.address)
        assertEquals("Example", invite.name)
        assertNull(invite.channelId)
    }

    @Test
    fun parsesAnInviteToAChannel() {
        val invite = TeamSpeakInviteService.parse(
            """{"type":"server","address":"ts.example.com:9988","name":"Example","channel_id":42,"channel_name":"Movies"}""",
            now,
        )!!
        assertEquals("ts.example.com:9988", invite.address)
        assertEquals(42L, invite.channelId)
        assertEquals("Movies", invite.channelName)
    }

    @Test
    fun anExpiredInviteIsNoInvite() {
        assertNull(
            TeamSpeakInviteService.parse(
                """{"type":"server","address":"ts.example.com","expires":${now - 1}}""",
                now,
            )
        )
    }

    @Test
    fun anInviteWithoutAnAddressIsNoInvite() {
        assertNull(TeamSpeakInviteService.parse("""{"type":"server","name":"Example"}""", now))
        assertNull(TeamSpeakInviteService.parse("""{"type":"channel","address":"a.b"}""", now))
    }

    @Test
    fun rubbishIsNoInvite() {
        assertNull(TeamSpeakInviteService.parse("not json at all", now))
        assertNull(TeamSpeakInviteService.parse("", now))
        assertNull(TeamSpeakInviteService.parse("[]", now))
    }

    @Test
    fun anInviteWithoutAnExpiryNeverExpires() {
        val invite = TeamSpeakInviteService.parse("""{"type":"server","address":"a.b"}""", now)!!
        assertEquals("a.b", invite.address)
    }

    /**
     * A hostname with no port in the invite: the port is left off, so the
     * server's own DNS entry (the `_ts3._udp.` SRV record, or TSDNS) says
     * which host and port to use. Forcing the default 9987 there breaks any
     * server whose SRV record names a different port.
     */
    @Test
    fun anAddressWithoutAPortHasNoExplicitPort() {
        val invite = TeamSpeakInviteService.parse("""{"type":"server","address":"ts.example.com"}""", now)!!
        assertNull(invite.explicitPort)
        assertEquals("ts.example.com", invite.host)
    }

    @Test
    fun anAddressWithAPortKeepsIt() {
        val invite = TeamSpeakInviteService.parse("""{"type":"server","address":"ts.example.com:9988"}""", now)!!
        assertEquals(9988, invite.explicitPort)
        assertEquals("ts.example.com", invite.host)
    }

    @Test
    fun anIpv6AddressIsNotMistakenForAPort() {
        val invite = TeamSpeakInviteService.parse("""{"type":"server","address":"2001:db8::1"}""", now)!!
        assertNull(invite.explicitPort)
        assertEquals("2001:db8::1", invite.host)
    }

    @Test
    fun theLookupUrlAsksTheInviteService() {
        assertEquals(
            "https://invites.teamspeak.com/query/?id=Ab3dE9xY",
            TeamSpeakInviteService.lookupUrl("Ab3dE9xY"),
        )
        // A code with characters that need escaping is escaped.
        assertTrueContains("id=a%2Fb", TeamSpeakInviteService.lookupUrl("a/b"))
    }

    private fun assertTrueContains(needle: String, haystack: String) {
        org.junit.Assert.assertTrue("expected $haystack to contain $needle", haystack.contains(needle))
    }
}
