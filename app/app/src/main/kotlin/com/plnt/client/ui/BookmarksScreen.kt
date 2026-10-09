package com.plnt.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.plnt.client.data.NicknameGenerator
import com.plnt.client.model.Bookmark
import com.plnt.client.ui.theme.Bg
import com.plnt.client.ui.theme.Bone
import com.plnt.client.ui.theme.BoneFaint
import com.plnt.client.ui.theme.BoneMuted
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.DotStale
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.Mauve
import com.plnt.client.ui.theme.Oxblood
import com.plnt.client.ui.theme.Sage
import com.plnt.client.ui.theme.SurfaceDark
import com.plnt.client.ui.theme.SurfaceHigh
import com.plnt.client.ui.theme.SurfaceRaised

/**
 * Servers list: a card per bookmark, tap to connect, swipe right to edit and
 * left to delete (delete confirms). Adding lives in the app bar and in the
 * empty state, never as a FAB — the thumb zone is reserved for PTT once
 * connected and the two screens should not fight over it.
 */
@Composable
fun BookmarksScreen(
    bookmarks: List<Bookmark>,
    sessionConnected: Set<String>,
    onConnect: (Bookmark) -> Unit,
    onSave: (Bookmark) -> Unit,
    onDelete: (String) -> Unit,
    newId: () -> String,
    onOpenSettings: () -> Unit,
) {
    var editing by remember { mutableStateOf<Bookmark?>(null) }
    var isNew by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<Bookmark?>(null) }

    fun startAdd() {
        editing = Bookmark(
            id = newId(),
            label = "",
            address = "",
            port = 9987,
            nickname = NicknameGenerator.random(),
        )
        isNew = true
    }

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg),
                title = {
                    Column {
                        Text("PLNT", style = MaterialTheme.typography.titleLarge, color = Bone)
                        Text("Servers", style = MaterialTheme.typography.labelMedium, color = BoneMuted)
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = BoneMuted)
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Gold)
                            .clickable { startAdd() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add server", tint = Bg)
                    }
                },
            )
        },
    ) { padding ->
        if (bookmarks.isEmpty()) {
            EmptyState(modifier = Modifier.fillMaxSize().padding(padding), onAdd = ::startAdd)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(bookmarks, key = { it.id }) { bookmark ->
                    SwipeableBookmarkRow(
                        bookmark = bookmark,
                        connectedThisSession = bookmark.id in sessionConnected,
                        onConnect = { onConnect(bookmark) },
                        onEdit = { editing = bookmark; isNew = false },
                        onDelete = { pendingDelete = bookmark },
                    )
                }
                item {
                    Text(
                        "Swipe a server right to edit, left to remove.",
                        color = BoneFaint,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    )
                }
            }
        }
    }

    pendingDelete?.let { target ->
        // A stray horizontal drag on a list you meant to scroll used to wipe a
        // server outright — a bookmark carries a nickname and a password nobody
        // wants to retype. Deleting is the one destructive action here, so it
        // is the one that asks.
        AlertDialog(
            containerColor = SurfaceRaised,
            shape = RoundedCornerShape(20.dp),
            onDismissRequest = { pendingDelete = null },
            title = { Text("Remove server?", color = Bone, style = MaterialTheme.typography.titleMedium) },
            text = { Text(target.label.ifBlank { target.address }, color = BoneMuted) },
            confirmButton = {
                TextButton(onClick = { onDelete(target.id); pendingDelete = null }) {
                    Text("Remove", color = Oxblood, style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancel", color = BoneMuted, style = MaterialTheme.typography.labelLarge)
                }
            },
        )
    }

    editing?.let { current ->
        BookmarkSheet(
            initial = current,
            isNew = isNew,
            onDismiss = { editing = null },
            onSave = { onSave(it); editing = null },
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier, onAdd: () -> Unit) {
    Column(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(96.dp).clip(CircleShape).background(SurfaceRaised),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Dns, contentDescription = null, tint = Gold, modifier = Modifier.size(44.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("No servers yet", style = MaterialTheme.typography.titleLarge, color = Bone)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Add a TeamSpeak server and PLNT keeps the nickname and password with it.",
            color = BoneMuted,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(28.dp))
        PrimaryButton("Add a server", onClick = onAdd, icon = Icons.Filled.Add)
    }
}

@Composable
private fun SwipeableBookmarkRow(
    bookmark: Bookmark,
    connectedThisSession: Boolean,
    onConnect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    // Reveal, not dismiss: confirmValueChange fires the action and returns false
    // so the row snaps back instead of disappearing.
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> onEdit()
                SwipeToDismissBoxValue.EndToStart -> onDelete()
                SwipeToDismissBoxValue.Settled -> Unit
            }
            false
        },
    )

    SwipeToDismissBox(
        state = swipeState,
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        backgroundContent = {
            val toDelete = swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (toDelete) Oxblood.copy(alpha = 0.25f) else Mauve.copy(alpha = 0.2f)),
                contentAlignment = if (toDelete) Alignment.CenterEnd else Alignment.CenterStart,
            ) {
                Row(
                    modifier = Modifier.width(96.dp).fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        if (toDelete) Icons.Filled.Delete else Icons.Filled.Edit,
                        contentDescription = null,
                        tint = if (toDelete) Oxblood else Mauve,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (toDelete) "Remove" else "Edit",
                        color = if (toDelete) Oxblood else Mauve,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        },
    ) {
        BookmarkRow(bookmark, connectedThisSession, onConnect)
    }
}

@Composable
private fun BookmarkRow(bookmark: Bookmark, connectedThisSession: Boolean, onConnect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .clickable { onConnect() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceHigh),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Dns, contentDescription = null, tint = Gold, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                bookmark.label.ifBlank { bookmark.address },
                color = Bone,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "${bookmark.address}:${bookmark.port}",
                color = BoneMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                StatusDot(if (connectedThisSession) Sage else DotStale)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (connectedThisSession) "connected this session" else "as ${bookmark.nickname}",
                    color = BoneFaint,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                )
            }
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = BoneFaint)
    }
}

@Composable
private fun BookmarkSheet(
    initial: Bookmark,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (Bookmark) -> Unit,
) {
    var label by remember { mutableStateOf(initial.label) }
    var address by remember { mutableStateOf(initial.address) }
    var port by remember { mutableStateOf(initial.port.toString()) }
    var nickname by remember { mutableStateOf(initial.nickname) }
    var password by remember { mutableStateOf(initial.serverPassword.orEmpty()) }
    var passwordVisible by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { SheetDragHandle() },
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
            Text(
                if (isNew) "Add server" else "Edit server",
                style = MaterialTheme.typography.titleLarge,
                color = Bone,
            )
            Spacer(modifier = Modifier.height(20.dp))
            PlntTextField("Label", label, { label = it }, placeholder = "Home")
            Spacer(modifier = Modifier.height(14.dp))
            PlntTextField("Address", address, { address = it }, placeholder = "teamspeak.example.com")
            Spacer(modifier = Modifier.height(14.dp))
            PlntTextField(
                "Port",
                port,
                { port = it.filter(Char::isDigit) },
                keyboardType = KeyboardType.Number,
            )
            Spacer(modifier = Modifier.height(14.dp))
            PlntTextField(
                "Nickname",
                nickname,
                { nickname = it },
                trailing = {
                    IconButton(
                        onClick = { nickname = NicknameGenerator.random() },
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "Generate nickname",
                            tint = BoneMuted,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
            )
            Spacer(modifier = Modifier.height(14.dp))
            PlntTextField(
                "Server password",
                password,
                { password = it },
                placeholder = "optional",
                visualTransformation = if (passwordVisible) VisualTransformation.None else PlntPasswordMask,
                trailing = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }, modifier = Modifier.size(28.dp)) {
                        Icon(
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = BoneMuted,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
            )

            Spacer(modifier = Modifier.height(28.dp))
            val canSave = address.isNotBlank()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton("Cancel", onClick = onDismiss, modifier = Modifier.weight(1f), color = BoneMuted)
                PrimaryButton(
                    if (isNew) "Add" else "Save",
                    enabled = canSave,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSave(
                            initial.copy(
                                label = label,
                                address = address.trim(),
                                port = port.toIntOrNull() ?: 9987,
                                nickname = nickname,
                                serverPassword = password.ifBlank { null },
                            ),
                        )
                    },
                )
            }
        }
    }
}
