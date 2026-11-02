package com.plnt.client.data

import com.plnt.client.model.Bookmark
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** PHA-4856 (WS8): bookmark secrets leave the plaintext blob and never come back to it. */
class BookmarkSecretsMigrationTest {
    private class FakeVault(var failWrites: Boolean = false, var failReads: Boolean = false) : SecretVault {
        val stored = mutableMapOf<String, BookmarkSecrets>()
        override fun readAll(): Map<String, BookmarkSecrets> {
            check(!failReads) { "keystore unavailable" }
            return stored.toMap()
        }
        override fun replaceAll(secrets: Map<String, BookmarkSecrets>): Boolean {
            check(!failWrites) { "keystore unavailable" }
            stored.clear()
            stored.putAll(secrets)
            return true
        }
    }

    private class Disk(var raw: String?) {
        var writes = 0
    }

    private fun repo(disk: Disk, vault: SecretVault) = BookmarkRepository(
        readPublic = { disk.raw },
        writePublic = { disk.raw = it; disk.writes++ },
        vault = vault,
    )

    private val withSecrets = Bookmark(
        id = "a", label = "Home", address = "ts.example.com", port = 9987, nickname = "me",
        serverPassword = "hunter2", channel = "Lobby", channelPassword = "chanpw", privilegeKey = "privkey",
        folder = "Friends",
    )
    private val plain = Bookmark(id = "b", label = "Open", address = "10.0.0.5", port = 9987, nickname = "me")

    /** Exactly what PlntDataStore wrote before WS8. */
    private val legacyBlob = """[
        {"id":"a","label":"Home","address":"ts.example.com","port":9987,"nickname":"me","password":"hunter2",
         "channel":"Lobby","channelpassword":"chanpw","privilegekey":"privkey","folder":"Friends"},
        {"id":"b","label":"Open","address":"10.0.0.5","port":9987,"nickname":"me","password":"",
         "channel":"","channelpassword":"","privilegekey":"","folder":""}
    ]"""

    private fun assertNoSecretsIn(raw: String?) {
        val text = checkNotNull(raw)
        for (s in listOf("hunter2", "chanpw", "privkey")) assertFalse("'$s' still in plaintext: $text", s in text)
        val arr = JSONArray(text)
        for (i in 0 until arr.length()) {
            for (field in listOf("password", "channelpassword", "privilegekey")) {
                assertFalse("field $field still written", arr.getJSONObject(i).has(field))
            }
        }
    }

    @Test fun legacyPlaintextIsMovedIntoTheVaultAndScrubbed() = runBlocking {
        val disk = Disk(legacyBlob)
        val vault = FakeVault()
        val loaded = repo(disk, vault).load()

        assertEquals(listOf(withSecrets, plain), loaded)
        assertNoSecretsIn(disk.raw)
        assertEquals(BookmarkSecrets("hunter2", "chanpw", "privkey"), vault.stored["a"])
        // A bookmark with no secrets leaves no vault entry.
        assertNull(vault.stored["b"])
    }

    @Test fun migratedStoreReadsBackIdenticallyAndIsNotRewrittenAgain() = runBlocking {
        val disk = Disk(legacyBlob)
        val vault = FakeVault()
        repo(disk, vault).load()
        val writesAfterMigration = disk.writes

        val again = repo(disk, vault).load()
        assertEquals(listOf(withSecrets, plain), again)
        assertEquals("a migrated store is not rewritten on every load", writesAfterMigration, disk.writes)
    }

    @Test fun migrationWaitsWhenTheVaultCannotBeWritten() = runBlocking {
        val disk = Disk(legacyBlob)
        val vault = FakeVault(failWrites = true)
        val loaded = repo(disk, vault).load()

        // Nothing lost: the old blob stays put and the move is retried next time.
        assertEquals(legacyBlob, disk.raw)
        assertEquals(listOf(withSecrets, plain), loaded)

        vault.failWrites = false
        repo(disk, vault).load()
        assertNoSecretsIn(disk.raw)
        assertEquals("hunter2", vault.stored["a"]?.serverPassword)
    }

    @Test fun migrationWaitsWhenTheVaultCannotBeRead() = runBlocking {
        val disk = Disk(legacyBlob)
        val vault = FakeVault(failReads = true)
        repo(disk, vault).load()
        // A blind replace could wipe secrets it failed to read; leave everything.
        assertEquals(legacyBlob, disk.raw)
        assertTrue(vault.stored.isEmpty())
    }

    @Test fun saveNeverWritesSecretsInTheClear() = runBlocking {
        val disk = Disk(null)
        val vault = FakeVault()
        assertTrue(repo(disk, vault).save(listOf(withSecrets, plain)))
        assertNoSecretsIn(disk.raw)
        assertEquals(listOf(withSecrets, plain), repo(disk, vault).load())
    }

    @Test fun saveFailsClosedWhenTheKeystoreIsUnavailable() = runBlocking {
        val disk = Disk(null)
        val vault = FakeVault(failWrites = true)
        val stored = repo(disk, vault).save(listOf(withSecrets))

        assertFalse("caller must be told the secrets were not kept", stored)
        // The bookmark itself is kept; its secrets are not, anywhere.
        assertNoSecretsIn(disk.raw)
        vault.failWrites = false
        val back = repo(disk, vault).load().single()
        assertEquals(withSecrets.copy(serverPassword = null, channelPassword = null, privilegeKey = null), back)
    }

    @Test fun deletingABookmarkDropsItsSecrets() = runBlocking {
        val disk = Disk(null)
        val vault = FakeVault()
        val r = repo(disk, vault)
        r.save(listOf(withSecrets, plain))
        r.save(listOf(plain))
        assertTrue(vault.stored.isEmpty())
    }

    @Test fun vaultEntryRoundTrips() {
        val s = BookmarkSecrets("p", null, "k")
        assertEquals(s, BookmarkSecrets.fromJson(s.toJson()))
        assertNull(BookmarkSecrets.of(plain))
    }
}
