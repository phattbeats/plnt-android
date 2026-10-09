package com.plnt.client.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ServerLinkTest {
    @Test
    fun parsesFullTeamSpeakLink() {
        val link = ServerLink.parse(
            "ts3server://ts.example.com?port=9988&nickname=b-mech&password=s%20cret" +
                "&channel=Movies%2FLobby&channelpassword=pop&addbookmark=Movie%20night",
        )!!
        assertEquals("ts.example.com", link.address)
        assertEquals(9988, link.port)
        assertEquals("b-mech", link.nickname)
        assertEquals("s cret", link.serverPassword)
        assertEquals("Movies/Lobby", link.channel)
        assertEquals("pop", link.channelPassword)
        assertEquals("Movie night", link.label)
    }

    @Test
    fun bareHostDefaultsPort() {
        assertEquals(ServerLink("ts.example.com"), ServerLink.parse("ts3server://ts.example.com"))
        assertEquals(ServerLink("ts.example.com"), ServerLink.parse("TS3SERVER://ts.example.com/"))
    }

    @Test
    fun inlinePortAndIpv6() {
        assertEquals(9000, ServerLink.parse("ts3server://1.2.3.4:9000")!!.port)
        val v6 = ServerLink.parse("ts3server://[2001:db8::1]:9001")!!
        assertEquals("2001:db8::1", v6.address)
        assertEquals(9001, v6.port)
        // Query port wins over an inline one, like the desktop client.
        assertEquals(9002, ServerLink.parse("ts3server://1.2.3.4:9000?port=9002")!!.port)
    }

    @Test
    fun otherSchemesAndConnectShape() {
        assertEquals("a.b", ServerLink.parse("tsserver://a.b")!!.address)
        val c = ServerLink.parse("teamspeak://connect?address=a.b:9100")!!
        assertEquals("a.b", c.address)
        assertEquals(9100, c.port)
    }

    @Test
    fun rejectsJunk() {
        assertNull(ServerLink.parse("https://ts.example.com"))
        assertNull(ServerLink.parse("ts3server://"))
        assertNull(ServerLink.parse("ts3server://bad host"))
        assertNull(ServerLink.parse("ts3server://a.b?port=70000"))
        assertNull(ServerLink.find("no link here"))
        assertNull(ServerLink.find(null))
    }

    @Test
    fun findsLinkInsideAMessage() {
        val text = "Join us for movie night! ts3server://ts.example.com?port=9987&channel=Movies. See you"
        val link = ServerLink.find(text)!!
        assertEquals("ts.example.com", link.address)
        assertEquals("Movies", link.channel)
    }

    @Test
    fun addbookmarkFlagIsNotALabel() {
        assertNull(ServerLink.parse("ts3server://a.b?addbookmark=1")!!.label)
    }

    @Test
    fun roundTripsAndNeverSharesNickname() {
        val original = ServerLink(
            address = "ts.example.com",
            port = 9987,
            nickname = "Brandon",
            serverPassword = "p&w+d =",
            channel = "Movies/Lobby",
            channelPassword = "x",
            label = "Movie night",
        )
        val uri = original.toUri()
        assertEquals(original.copy(nickname = null), ServerLink.parse(uri))
        assertEquals(false, uri.contains("Brandon"))
        assertEquals(ServerLink("2001:db8::1", 9001), ServerLink.parse(ServerLink("2001:db8::1", 9001).toUri()))
    }
}
