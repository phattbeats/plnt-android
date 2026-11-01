package com.plnt.client.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plnt.client.admin.AdminOp
import com.plnt.client.model.ChannelNode
import com.plnt.client.model.ClientRow
import com.plnt.client.ui.theme.Brand
import com.plnt.client.ui.theme.Danger
import com.plnt.client.ui.theme.SurfaceDark
import com.plnt.client.ui.theme.TextMuted
import com.plnt.client.ui.theme.TextNormal

/**
 * PHA-4854: what a long-press landed on. Admin actions are offered on every
 * row; the server decides whether the caller may, and a refusal comes back as
 * a readable error (see `AdminCommands.describe`).
 */
sealed class AdminTarget {
    data class User(val client: ClientRow) : AdminTarget()
    data class Channel(val node: ChannelNode, val parentId: Long, val lastSiblingId: Long?, val firstSiblingId: Long?) : AdminTarget()
}

/** A free-text step an admin action needs before it can be sent. */
sealed class AdminForm {
    data class CreateChannel(val parent: ChannelNode?) : AdminForm()
    data class RenameChannel(val node: ChannelNode) : AdminForm()
    data class ChannelPassword(val node: ChannelNode) : AdminForm()
    data class Info(val title: String, val lines: List<String>) : AdminForm()
}

private data class MenuItem(val label: String, val danger: Boolean = false, val onClick: () -> Unit)

@Composable
fun AdminMenuDialog(
    target: AdminTarget,
    ownChannelId: Long?,
    onAdmin: (AdminOp) -> Unit,
    onForm: (AdminForm) -> Unit,
    onDismiss: () -> Unit,
) {
    val items: List<MenuItem>
    val title: String
    when (target) {
        is AdminTarget.User -> {
            val id = target.client.clientId
            title = target.client.name
            items = buildList {
                add(MenuItem("Info") {
                    onForm(AdminForm.Info(target.client.name, listOf("Client id: $id")))
                })
                if (ownChannelId != null) {
                    add(MenuItem("Move to my channel") { onAdmin(AdminOp.MoveClient(id, ownChannelId)) })
                }
                add(MenuItem("Kick from channel", danger = true) { onAdmin(AdminOp.Kick(id, fromServer = false)) })
                add(MenuItem("Kick from server", danger = true) { onAdmin(AdminOp.Kick(id, fromServer = true)) })
                add(MenuItem("Ban for 1 hour", danger = true) { onAdmin(AdminOp.Ban(id, 3600)) })
                add(MenuItem("Ban for 1 day", danger = true) { onAdmin(AdminOp.Ban(id, 86400)) })
                add(MenuItem("Ban permanently", danger = true) { onAdmin(AdminOp.Ban(id, 0)) })
            }
        }
        is AdminTarget.Channel -> {
            val node = target.node
            title = node.name
            items = buildList {
                add(MenuItem("Info") {
                    onForm(
                        AdminForm.Info(
                            node.name,
                            listOf(
                                "Channel id: ${node.id}",
                                "Password protected: ${if (node.hasPassword) "yes" else "no"}",
                                "Talk power needed: ${node.talkPower}",
                                "People in it now: ${node.clients.size}",
                                "Sub-channels: ${node.children.size}",
                            ),
                        ),
                    )
                })
                add(MenuItem("Create sub-channel…") { onForm(AdminForm.CreateChannel(node)) })
                add(MenuItem("Rename…") { onForm(AdminForm.RenameChannel(node)) })
                add(MenuItem(if (node.hasPassword) "Change or remove password…" else "Set password…") {
                    onForm(AdminForm.ChannelPassword(node))
                })
                if (target.firstSiblingId != null && target.firstSiblingId != node.id) {
                    add(MenuItem("Move to top") { onAdmin(AdminOp.MoveChannel(node.id, target.parentId, 0)) })
                }
                if (target.lastSiblingId != null && target.lastSiblingId != node.id) {
                    add(MenuItem("Move to bottom") {
                        onAdmin(AdminOp.MoveChannel(node.id, target.parentId, target.lastSiblingId))
                    })
                }
                add(MenuItem("Delete channel", danger = true) { onAdmin(AdminOp.DeleteChannel(node.id)) })
            }
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text(title, color = TextNormal, maxLines = 1) },
        text = {
            Column {
                items.forEach { item ->
                    Text(
                        item.label,
                        color = if (item.danger) Danger else TextNormal,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onDismiss()
                                item.onClick()
                            }
                            .padding(vertical = 12.dp),
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close", color = TextMuted) } },
    )
}

/** One-field form for the admin actions that need text; or a read-only info card. */
@Composable
fun AdminFormDialog(
    form: AdminForm,
    onAdmin: (AdminOp) -> Unit,
    onDismiss: () -> Unit,
) {
    if (form is AdminForm.Info) {
        AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = SurfaceDark,
            title = { Text(form.title, color = TextNormal, maxLines = 1) },
            text = {
                Column {
                    form.lines.forEach { Text(it, color = TextMuted, style = MaterialTheme.typography.bodyMedium) }
                }
            },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Close", color = Brand) } },
        )
        return
    }
    val (title, label, initial, confirm) = when (form) {
        is AdminForm.CreateChannel ->
            FormSpec(if (form.parent != null) "New sub-channel of \"${form.parent.name}\"" else "New channel", "Channel name", "", "Create")
        is AdminForm.RenameChannel -> FormSpec("Rename channel", "Channel name", form.node.name, "Rename")
        is AdminForm.ChannelPassword ->
            FormSpec("Channel password", "New password (empty removes it)", "", "Save")
        is AdminForm.Info -> return
    }
    var value by remember(form) { mutableStateOf(initial) }
    val password = form is AdminForm.ChannelPassword
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text(title, color = TextNormal) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text(label) },
                singleLine = true,
                visualTransformation = if (password) {
                    androidx.compose.ui.text.input.PasswordVisualTransformation()
                } else {
                    androidx.compose.ui.text.input.VisualTransformation.None
                },
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    val op = when (form) {
                        is AdminForm.CreateChannel -> AdminOp.CreateChannel(value, parentId = form.parent?.id)
                        is AdminForm.RenameChannel -> AdminOp.EditChannel(form.node.id, name = value)
                        is AdminForm.ChannelPassword -> AdminOp.EditChannel(form.node.id, password = value)
                        is AdminForm.Info -> null
                    }
                    if (op != null) onAdmin(op)
                },
            ) { Text(confirm, color = Brand) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
    )
}

private data class FormSpec(val title: String, val label: String, val initial: String, val confirm: String)

/** Confirmation for a destructive operation; kick and ban take an optional reason. */
@Composable
fun AdminConfirmDialog(
    op: AdminOp,
    targetName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var reason by remember(op) { mutableStateOf("") }
    val takesReason = op is AdminOp.Kick || op is AdminOp.Ban
    val what = when (op) {
        is AdminOp.Ban ->
            if (op.seconds == 0L) "Ban $targetName permanently?" else "Ban $targetName for ${durationText(op.seconds)}?"
        is AdminOp.Kick ->
            if (op.fromServer) "Kick $targetName from the server?" else "Kick $targetName from the channel?"
        is AdminOp.DeleteChannel -> "Delete \"$targetName\"? Its sub-channels go with it."
        else -> "${op.label.replaceFirstChar { it.uppercase() }}?"
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = { Text("Are you sure?", color = TextNormal) },
        text = {
            Column {
                Text(what, color = TextMuted, style = MaterialTheme.typography.bodyMedium)
                if (takesReason) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it.take(80) },
                        label = { Text("Reason (optional)") },
                        singleLine = true,
                    )
                }
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(reason) }) { Text(op.label.replaceFirstChar { it.uppercase() }, color = Danger) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
    )
}

private fun durationText(seconds: Long): String = when {
    seconds % 86400 == 0L -> "${seconds / 86400} day(s)"
    seconds % 3600 == 0L -> "${seconds / 3600} hour(s)"
    else -> "${seconds / 60} minute(s)"
}
