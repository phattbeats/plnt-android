package com.plnt.client.admin

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * PHA-4854. The "allowed" and "denied" replies are the exact `error` commands
 * a TeamSpeak 3 server sends to an admin and to a guest; the commands are
 * asserted as they go to core `sendRawCommand`.
 */
class AdminTest {
    private fun build(op: AdminOp) = AdminCommands.build(op, 7)

    private fun invalid(op: AdminOp) {
        try {
            build(op)
            fail("expected AdminInvalid for $op")
        } catch (_: AdminInvalid) {
        }
    }

    @Test fun passwordHashIsTeamSpeaksBase64Sha1() {
        // sha1("hunter2") = f3bbbd66a63d4bf1747940578ec3d0103530e21d
        assertEquals("87u9ZqY9S/F0eUBXjsPQEDUw4h0=", AdminCommands.hashPassword("hunter2"))
    }

    @Test fun kickFromChannelAndFromServerUseDifferentReasonIds() {
        val ch = build(AdminOp.Kick(9, fromServer = false, reason = "afk"))
        assertEquals("clientkick", ch.name)
        assertEquals("4", ch.args["reasonid"])
        assertEquals("afk", ch.args["reasonmsg"])
        val srv = build(AdminOp.Kick(9, fromServer = true))
        assertEquals("5", srv.args["reasonid"])
        assertFalse(srv.args.containsKey("reasonmsg"))
        assertEquals("adm7", srv.args["return_code"])
        assertEquals("adm7", srv.returnCode)
    }

    @Test fun banIsPermanentWithoutATimeAndTimedWithOne() {
        val perm = build(AdminOp.Ban(3, 0, "spam bot"))
        assertEquals("banclient", perm.name)
        assertFalse(perm.args.containsKey("time"))
        assertEquals("spam bot", perm.args["banreason"])
        assertEquals("600", build(AdminOp.Ban(3, 600)).args["time"])
    }

    @Test fun overlongOrInvalidInputIsRefusedBeforeSending() {
        val long = "x".repeat(81)
        invalid(AdminOp.Kick(1, true, long))
        invalid(AdminOp.Ban(1, 0, long))
        invalid(AdminOp.Ban(1, -5))
        invalid(AdminOp.CreateChannel("   "))
        invalid(AdminOp.CreateChannel("n".repeat(41)))
        invalid(AdminOp.CreateChannel("ok", maxClients = 0))
        invalid(AdminOp.EditChannel(3))
        invalid(AdminOp.MoveChannel(6, 6))
    }

    @Test fun movingAnotherClientHashesTheChannelPassword() {
        val c = build(AdminOp.MoveClient(5, 2, "hunter2"))
        assertEquals("clientmove", c.name)
        assertEquals("5", c.args["clid"])
        assertEquals("2", c.args["cid"])
        assertEquals("87u9ZqY9S/F0eUBXjsPQEDUw4h0=", c.args["cpw"])
        assertFalse(c.args.values.contains("hunter2"))
        assertFalse(build(AdminOp.MoveClient(5, 2, "")).args.containsKey("cpw"))
    }

    @Test fun channelCreateSendsParentFlagsAndHashedPassword() {
        val c = build(AdminOp.CreateChannel("Movie Night", 4, "secret", "Fridays", 12, true))
        assertEquals("channelcreate", c.name)
        assertEquals("Movie Night", c.args["channel_name"])
        assertEquals("4", c.args["cpid"])
        assertEquals("Fridays", c.args["channel_topic"])
        assertEquals("12", c.args["channel_maxclients"])
        assertEquals("1", c.args["channel_flag_permanent"])
        assertNotNull(c.args["channel_password"])
        assertFalse(c.args.values.contains("secret"))

        val root = build(AdminOp.CreateChannel("Lobby", permanent = false))
        assertFalse(root.args.containsKey("cpid"))
        assertEquals("0", root.args["channel_flag_permanent"])
    }

    @Test fun channelEditOnlySendsWhatChangedAndEmptyPasswordClears() {
        val rename = build(AdminOp.EditChannel(3, name = "Renamed"))
        assertEquals("channeledit", rename.name)
        assertEquals(setOf("cid", "channel_name", "return_code"), rename.args.keys)
        val clear = build(AdminOp.EditChannel(3, password = ""))
        assertEquals("0", clear.args["channel_flag_password"])
        assertFalse(clear.args.containsKey("channel_password"))
        val set = build(AdminOp.EditChannel(3, password = "pw"))
        assertEquals("1", set.args["channel_flag_password"])
    }

    @Test fun reorderAndDelete() {
        val m = build(AdminOp.MoveChannel(6, 2, 5))
        assertEquals("channelmove", m.name)
        assertEquals("5", m.args["order"])
        assertEquals("2", m.args["cpid"])
        val d = build(AdminOp.DeleteChannel(6, force = true))
        assertEquals("channeldelete", d.name)
        assertEquals("1", d.args["force"])
    }

    @Test fun onlyDestructiveOperationsNeedConfirmation() {
        assertTrue(AdminOp.Kick(1, false).needsConfirmation)
        assertTrue(AdminOp.Ban(1).needsConfirmation)
        assertTrue(AdminOp.DeleteChannel(1).needsConfirmation)
        assertFalse(AdminOp.MoveClient(1, 2).needsConfirmation)
        assertFalse(AdminOp.MoveChannel(1, 0).needsConfirmation)
        assertFalse(AdminOp.CreateChannel("a").needsConfirmation)
    }

    @Test fun allowedRoleGetsSuccess() {
        val r = AdminCommands.describe(
            AdminOp.Kick(9, true),
            mapOf("id" to "0", "msg" to "ok", "return_code" to "adm7"),
        )
        assertTrue(r.ok)
        assertFalse(r.denied)
    }

    @Test fun deniedRoleGetsAReadablePermissionErrorNamingTheOperation() {
        val r = AdminCommands.describe(
            AdminOp.Ban(9),
            mapOf("id" to "2568", "msg" to "insufficient client permissions", "failed_permid" to "187"),
        )
        assertFalse(r.ok)
        assertTrue(r.denied)
        assertEquals(2568, r.errorId)
        assertTrue(r.message, r.message.contains("ban user"))
        assertTrue(r.message, r.message.contains("187"))
        assertFalse(r.message.contains("insufficient client permissions"))
    }

    @Test fun otherFailuresAreNotCalledPermissionProblems() {
        val del = AdminOp.DeleteChannel(4)
        val notEmpty = AdminCommands.describe(del, mapOf("id" to "772", "msg" to "channel not empty"))
        assertFalse(notEmpty.denied)
        assertTrue(notEmpty.message.contains("isn't empty"))
        val other = AdminCommands.describe(del, mapOf("id" to "9999", "msg" to "weird"))
        assertFalse(other.denied)
        assertTrue(other.message.contains("weird"))
        assertTrue(AdminCommands.describe(del, mapOf("id" to "9999")).message.contains("9999"))
    }

    @Test fun returnCodesRoundTrip() {
        assertEquals(42, AdminCommands.requestIdOf(AdminCommands.returnCode(42)))
        assertNull(AdminCommands.requestIdOf("token"))
        assertNull(AdminCommands.requestIdOf(null))
        assertNull(AdminCommands.requestIdOf("admx"))
    }
}
