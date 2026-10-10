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
import androidx.compose.material.icons.filled.Share
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
import com.plnt.client.model.ConnectionPhase
import com.plnt.client.model.ServerConnectionStatus
import com.plnt.client.ui.theme.Bg
import com.plnt.client.ui.theme.OnBrand
import com.plnt.client.ui.theme.Warning
import com.plnt.client.ui.theme.TextNormal
import com.plnt.client.ui.theme.TextFaint
import com.plnt.client.ui.theme.TextMuted
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.DotStale
import com.plnt.client.ui.theme.Brand
import com.plnt.client.ui.theme.Accent
import com.plnt.client.ui.theme.Danger
import com.plnt.client.ui.theme.Speaking
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
    connectionStatus: ServerConnectionStatus,
    lastError: String?,
    onConnect: (Bookmark) -> Unit,
    onSave: (Bookmark) -> Unit,
    onDelete: (String) -> Unit,
    newId: () -> String,
    onOpenSettings: () -> Unit,
) {
    var editing by remember { mutableStateOf<Bookmark?>(null) }
    var isNew by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<Bookmark?>(null) }
    var sharing by remember { mutableStateOf<Bookmark?>(null) }

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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TeamSpeakMark(size = 32.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("TeamSpeak", style = MaterialTheme.typography.titleLarge, color = TextNormal)
                            Text("Servers", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = TextMuted)
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brand)
                            .clickable { startAdd() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add server", tint = OnBrand)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (connectionStatus.busy) {
                Text(
                    if (connectionStatus.phase == ConnectionPhase.RECONNECTING) "Reconnecting…" else "Connecting…",
                    color = Brand,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                )
            }
            lastError?.let { error ->
                Text(
                    error,
                    color = TextNormal,
                    modifier = Modifier.fillMaxWidth().background(Danger.copy(alpha = 0.25f)).padding(16.dp),
                )
            }
            if (bookmarks.isEmpty()) {
                EmptyState(modifier = Modifier.fillMaxSize(), onAdd = ::startAdd)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(bookmarks, key = { it.id }) { bookmark ->
                        SwipeableBookmarkRow(
                            bookmark = bookmark,
                            connected = connectionStatus.isConnected(bookmark.id),
                            pending = connectionStatus.isPending(bookmark.id),
                            connectEnabled = !connectionStatus.busy,
                            onConnect = { onConnect(bookmark) },
                            onEdit = { editing = bookmark; isNew = false },
                            onDelete = { pendingDelete = bookmark },
                        )
                    }
                    item {
                        Text(
                            "Swipe a server right to edit, left to remove.",
                            color = TextFaint,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        )
                    }
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
            title = { Text("Remove server?", color = TextNormal, style = MaterialTheme.typography.titleMedium) },
            text = { Text(target.label.ifBlank { target.address }, color = TextMuted) },
            confirmButton = {
                TextButton(onClick = { onDelete(target.id); pendingDelete = null }) {
                    Text("Remove", color = Danger, style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Cancel", color = TextMuted, style = MaterialTheme.typography.labelLarge)
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
            onShare = if (isNew) null else { { editing = null; sharing = current } },
        )
    }

    sharing?.let { target ->
        ShareInviteSheet(
            link = com.plnt.client.model.ServerLink(
                address = target.address,
                port = target.port,
                serverPassword = target.serverPassword,
                label = target.label.ifBlank { null },
            ),
            onDismiss = { sharing = null },
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
            TeamSpeakMark(size = 56.dp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("No servers yet", style = MaterialTheme.typography.titleLarge, color = TextNormal)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Add a TeamSpeak server and its nickname and password are kept with it.",
            color = TextMuted,
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
    connected: Boolean,
    pending: Boolean,
    connectEnabled: Boolean,
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
                    .background(if (toDelete) Danger.copy(alpha = 0.25f) else Accent.copy(alpha = 0.2f)),
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
                        tint = if (toDelete) Danger else Accent,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (toDelete) "Remove" else "Edit",
                        color = if (toDelete) Danger else Accent,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        },
    ) {
        BookmarkRow(bookmark, connected, pending, connectEnabled, onConnect)
    }
}

@Composable
private fun BookmarkRow(bookmark: Bookmark, connected: Boolean, pending: Boolean, connectEnabled: Boolean, onConnect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceRaised)
            .clickable(enabled = connectEnabled) { onConnect() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ServerTile(bookmark.label.ifBlank { bookmark.address }, connected)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                bookmark.label.ifBlank { bookmark.address },
                color = TextNormal,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "${bookmark.address}:${bookmark.port}",
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                StatusDot(if (connected) Speaking else if (pending) Warning else DotStale)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    if (connected) "connected" else if (pending) "connecting…" else "as ${bookmark.nickname}",
                    color = TextFaint,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                )
            }
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextFaint)
    }
}

@Composable
private fun BookmarkSheet(
    initial: Bookmark,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (Bookmark) -> Unit,
    onShare: (() -> Unit)? = null,
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
                color = TextNormal,
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
                            tint = TextMuted,
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
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
            )

            if (onShare != null) {
                Spacer(modifier = Modifier.height(20.dp))
                SecondaryButton(
                    "Share invite link",
                    icon = Icons.Filled.Share,
                    color = Brand,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onShare,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
            val canSave = address.isNotBlank()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton("Cancel", onClick = onDismiss, modifier = Modifier.weight(1f), color = TextMuted)
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

/**
 * Discord-style server tile: the server's initials on a squircle in a colour
 * derived from its name, so each server is recognisable at a glance. The
 * corners tighten (circle -> squircle) on the live server, as Discord's
 * guild rail does for the selected guild.
 */
@Composable
private fun ServerTile(name: String, live: Boolean) {
    val initials = name
        .split(' ', '.', '-', '_')
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(if (live) 16.dp else 24.dp))
            .background(avatarTint(name)),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, color = OnBrand, style = MaterialTheme.typography.titleSmall)
    }
}
