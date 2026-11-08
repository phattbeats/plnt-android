package com.plnt.client.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * PHA-4849: finding the channel an invite named.
 *
 * An invite names a channel by id or by a path of names, and the two need
 * different handling: the server's connect-time channel only understands
 * names, so an id is joined after the tree arrives.
 */
class ChannelPathTest {
    private fun node(
        id: Long,
        name: String,
        children: List<ChannelNode> = emptyList(),
        hasPassword: Boolean = false,
    ) = ChannelNode(
        id = id,
        name = name,
        hasPassword = hasPassword,
        talkPower = 0,
        children = children,
    )

    private val tree = listOf(
        node(
            1, "Default Channel",
            children = listOf(
                node(2, "Movies", children = listOf(node(3, "Lobby"))),
                node(4, "Music"),
            ),
        ),
    )
    @Test
    fun anIdIsRecognisedAsAnId() {
        assertEquals(42L, ChannelPath.idOf("42"))
        assertEquals(42L, ChannelPath.idOf(" 42 "))
        assertNull(ChannelPath.idOf("0"))
        assertNull(ChannelPath.idOf("-1"))
        assertNull(ChannelPath.idOf("Movies"))
        assertNull(ChannelPath.idOf(null))
    }

    @Test
    fun aPathIsRecognisedAsAPath() {
        assertEquals(listOf("Movies", "Lobby"), ChannelPath.namesOf("Movies/Lobby"))
        assertEquals(listOf("Movies", "Lobby"), ChannelPath.namesOf("/Movies/Lobby/"))
        // An id is not a path.
        assertNull(ChannelPath.namesOf("42"))
        assertNull(ChannelPath.namesOf(""))
        assertNull(ChannelPath.namesOf(null))
    }

    @Test
    fun onlyAPathCanBeAskedForAtConnectTime() {
        assertTrue(ChannelPath.isConnectTime("Movies/Lobby"))
        assertFalse(ChannelPath.isConnectTime("42"))
        assertFalse(ChannelPath.isConnectTime(null))
    }

    @Test
    fun anIdIsResolvedAgainstTheTree() {
        assertEquals(3L, ChannelPath.resolve("3", tree)?.id)
        assertEquals(4L, ChannelPath.resolve("4", tree)?.id)
        assertNull(ChannelPath.resolve("99", tree))
    }

    @Test
    fun aPathIsResolvedAgainstTheTree() {
        // A channel path is rooted at the top level, the way TeamSpeak's own
        // `default_channel` is: "Movies" means a top-level channel called
        // Movies, not one nested somewhere.
        assertEquals(1L, ChannelPath.resolve("Default Channel", tree)?.id)
        assertEquals(2L, ChannelPath.resolve("Default Channel/Movies", tree)?.id)
        assertEquals(3L, ChannelPath.resolve("Default Channel/Movies/Lobby", tree)?.id)
        // Names are matched case-insensitively — a link's casing is not the
        // server's.
        assertEquals(3L, ChannelPath.resolve("default channel/movies/lobby", tree)?.id)
        // A path that does not exist resolves to nothing rather than to the
        // wrong channel.
        assertNull(ChannelPath.resolve("Default Channel/Nonexistent", tree))
        assertNull(ChannelPath.resolve("Nonexistent", tree))
        // A nested channel is not reachable by its bare name.
        assertNull(ChannelPath.resolve("Movies", tree))
    }

    @Test
    fun aProtectedChannelIsStillFound() {
        val withPassword = listOf(
            node(1, "Default Channel", children = listOf(node(9, "Secret", hasPassword = true))),
        )
        assertEquals(9L, ChannelPath.resolve("Default Channel/Secret", withPassword)?.id)
        assertTrue(ChannelPath.resolve("Default Channel/Secret", withPassword)?.hasPassword == true)
    }
}
