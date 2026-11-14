package com.plnt.client.account

import com.plnt.client.model.Bookmark
import com.plnt.client.model.isAccountImport
import org.junit.Assert.*
import org.junit.Test

/**
 * The import policy: which synced servers may be added, which are already on
 * the phone, and what happens to the folder a server was synced under.
 */
class AccountImportPolicyTest {
    private fun preview(sync: String): AccountPreview {
        val client = AccountClient(AccountTransport { rpc, _ ->
            if (rpc == ReadRpc.LOGIN) AccountFixtures.loginReply.hex() else sync.hex()
        }, { true })
        return client.fetch("Person@Example.invalid", "SyntheticPassword!", "", "own-device")
    }

    @Test fun syncedFolderReferenceIsCarriedOntoTheBookmark() {
        val entry = preview(AccountFixtures.folderSyncReply).entries.single()
        assertEquals("synthetic-folder-id", entry.folder)
        assertEquals("synthetic-folder-id", entry.bookmark.folder)
    }

    @Test fun aServerSyncedWithoutAFolderHasNoFolder() {
        val entry = preview(AccountFixtures.syncReply).entries.single()
        assertNull(entry.folder)
        assertNull(entry.bookmark.folder)
    }

    @Test fun aServerThisPhoneAlreadyHasIsADuplicate() {
        val entry = preview(AccountFixtures.syncReply).entries.single()
        val saved = listOf(Bookmark("local-1", "My server", "voice.example.invalid", 9987, "Me"))
        assertEquals(setOf(entry.bookmark.id), duplicateServers(listOf(entry), saved))
    }

    @Test fun hostCaseDoesNotMakeADuplicateLookDifferent() {
        val entry = preview(AccountFixtures.syncReply).entries.single()
        val saved = listOf(Bookmark("local-1", "My server", "VOICE.Example.INVALID", 9987, "Me"))
        assertEquals(setOf(entry.bookmark.id), duplicateServers(listOf(entry), saved))
    }

    @Test fun aDifferentPortOrHostIsNotADuplicate() {
        val entry = preview(AccountFixtures.syncReply).entries.single()
        val otherPort = listOf(Bookmark("local-1", "My server", "voice.example.invalid", 9999, "Me"))
        val otherHost = listOf(Bookmark("local-1", "My server", "other.example.invalid", 9987, "Me"))
        assertTrue(duplicateServers(listOf(entry), otherPort).isEmpty())
        assertTrue(duplicateServers(listOf(entry), otherHost).isEmpty())
    }

    @Test fun importingTheSameAccountServerTwiceUpdatesItRatherThanDuplicating() {
        val entry = preview(AccountFixtures.syncReply).entries.single()
        // The same synced item coming back is an update of the entry this app
        // wrote last time, so it must not be skipped as a duplicate.
        assertTrue(entry.bookmark.isAccountImport())
        assertTrue(duplicateServers(listOf(entry), listOf(entry.bookmark)).isEmpty())
    }

    @Test fun aDifferentSavedServerAtTheSameHostIsADuplicate() {
        val entry = preview(AccountFixtures.syncReply).entries.single()
        // Same host and port, but a server this app did not import: importing
        // it would leave two entries for one host, so it is skipped.
        val manual = Bookmark("local-1", "My server", "voice.example.invalid", 9987, "Me")
        assertEquals(setOf(entry.bookmark.id), duplicateServers(listOf(entry), listOf(manual)))
    }

    @Test fun onlyAccountImportsCarryTheAccountMarker() {
        assertTrue("myts:abc".let { Bookmark(it, "l", "a", 1, "n") }.isAccountImport())
        assertFalse(Bookmark("local", "l", "a", 1, "n").isAccountImport())
    }

    @Test fun signingOutClearsTheImportedDataAndTheDeviceIdOnly() {
        val keys = setOf(
            "identity_pem",
            "myts_device",
            "myts:0123456789abcdef",
            "input_route",
        )
        assertEquals(setOf("myts_device", "myts:0123456789abcdef"), accountKeysToClear(keys))
    }

    @Test fun signingOutAnInstallThatNeverImportedAnythingClearsNothing() {
        assertTrue(accountKeysToClear(setOf("identity_pem", "input_route")).isEmpty())
    }
}
