package com.plnt.client.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PHA-4849: TeamSpeak's own link host, `tmspk.gg`, in both of its shapes,
 * plus the privilege key and channel id a link may carry.
 */
class TeamSpeakLinkTest {
    // ---- tmspk.gg, the "s=" shape: a server link -----------------------

    @Test
    fun parsesTheTmspkServerLink() {
        val link = ServerLink.parse("https://tmspk.gg/s=ts.example.com?port=9988&nickname=Lee")!!
        assertEquals("ts.example.com", link.address)
        assertEquals(9988, link.port)
        assertEquals("Lee", link.nickname)
        assertFalse(link.isInvite)
    }

    @Test
    fun parsesTheTmspkServerLinkWithoutAScheme() {
        val link = ServerLink.parse("tmspk.gg/s=ts.example.com?port=9988")!!
        assertEquals("ts.example.com", link.address)
        assertEquals(9988, link.port)
    }

    @Test
    fun parsesTheTmspkServerLinkWithTheSlashSpelling() {
        val link = ServerLink.parse("https://tmspk.gg/s/ts.example.com?port=9988")!!
        assertEquals("ts.example.com", link.address)
        assertEquals(9988, link.port)
    }

    // ---- tmspk.gg, the invite shape -------------------------------------

    @Test
    fun parsesTheTmspkInviteCode() {
        val link = ServerLink.parse("https://tmspk.gg/Ab3dE9xY")!!
        assertTrue(link.isInvite)
        assertEquals("Ab3dE9xY", link.inviteCode)
        // The server is not known until the invite service is asked.
        assertEquals("", link.address)
    }

    @Test
    fun parsesTheTmspkInviteCodeWithATrailingSlash() {
        val link = ServerLink.parse("https://tmspk.gg/Ab3dE9xY/")!!
        assertEquals("Ab3dE9xY", link.inviteCode)
    }

    @Test
    fun rejectsAnInviteCodeThatIsNotOne() {
        // Too short, and a path rather than a code.
        assertNull(ServerLink.parse("https://tmspk.gg/ab"))
        assertNull(ServerLink.parse("https://tmspk.gg/"))
        // A domain that merely ends in tmspk.gg is not tmspk.gg.
        assertNull(ServerLink.parse("https://tmspk.gg.example.com/Ab3dE9xY"))
    }

    @Test
    fun findsATmspkInviteInAPastedMessage() {
        val text = "Join us: https://tmspk.gg/Ab3dE9xY tonight"
        val link = ServerLink.find(text)!!
        assertTrue(link.isInvite)
        assertEquals("Ab3dE9xY", link.inviteCode)
    }

    @Test
    fun findsATmspkServerLinkInAPastedMessage() {
        val text = "here you go https://tmspk.gg/s=ts.example.com?port=9987"
        val link = ServerLink.find(text)!!
        assertEquals("ts.example.com", link.address)
    }

    // ---- privilege keys and channel ids ---------------------------------

    @Test
    fun readsThePrivilegeKey() {
        val link = ServerLink.parse("ts3server://a.b?token=P4U:abc123")!!
        assertEquals("P4U:abc123", link.privilegeKey)
    }

    @Test
    fun readsThePrivilegeKeyUnderItsOtherName() {
        val link = ServerLink.parse("ts3server://a.b?privilegekey=P4U:abc123")!!
        assertEquals("P4U:abc123", link.privilegeKey)
    }

    @Test
    fun readsTheChannelId() {
        val link = ServerLink.parse("ts3server://a.b?cid=42")!!
        assertEquals(42L, link.channelId)
        // A channel id of zero is not a channel.
        assertNull(ServerLink.parse("ts3server://a.b?cid=0")!!.channelId)
        assertNull(ServerLink.parse("ts3server://a.b?cid=nope")!!.channelId)
    }

    /**
     * A produced link must not carry a privilege key. The key grants the
     * holder the issuer's rights, and a link gets shared around.
     */
    @Test
    fun aProducedLinkNeverCarriesThePrivilegeKey() {
        val uri = ServerLink(
            address = "ts.example.com",
            port = 9987,
            privilegeKey = "P4U:secret",
            channelId = 42,
        ).toUri()
        assertFalse(uri.contains("P4U:secret"))
        assertFalse(uri.contains("token="))
        // The channel id is harmless, and worth carrying.
        assertTrue(uri.contains("cid=42"))
    }

    @Test
    fun theServerPasswordSurvivesTheRoundTrip() {
        val original = ServerLink(
            address = "ts.example.com",
            port = 9987,
            serverPassword = "p&w+d =",
            channelPassword = "c p",
        )
        val back = ServerLink.parse(original.toUri())!!
        assertEquals("p&w+d =", back.serverPassword)
        assertEquals("c p", back.channelPassword)
    }
}
