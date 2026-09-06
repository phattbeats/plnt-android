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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
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
import com.plnt.client.ui.theme.SurfaceRaised

/**
 * PHA-3076 screens 1–3: bookmarks (empty + populated) and the add/edit bottom
 * sheet. Rows are swipe-to-reveal Edit/Delete and a plain tap connects — no
 * per-row buttons. Delete confirms; edit and connect do not.
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

    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark),
                title = { Text("PLNT", style = MaterialTheme.typography.titleLarge, color = Bone) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = BoneMuted)
                    }
                    // The app bar "+" is the only entry point for adding a server
                    // (design §1) — deliberately not a FAB, since it is a rare,
                    // deliberate action and the thumb zone belongs to PTT.
                    IconButton(onClick = {
                        editing = Bookmark(
                            id = newId(),
                            label = "",
                            address = "",
                            port = 9987,
                            nickname = NicknameGenerator.random(),
                        )
                        isNew = true
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add server", tint = Mauve)
                    }
                },
            )
        },
    ) { padding ->
        if (bookmarks.isEmpty()) {
            EmptyState(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(bookmarks, key = { it.id }) { bookmark ->
                    SwipeableBookmarkRow(
                        bookmark = bookmark,
                        connectedThisSession = bookmark.id in sessionConnected,
                        onConnect = { onConnect(bookmark) },
                        onEdit = { editing = bookmark; isNew = false },
                        onDelete = { pendingDelete = bookmark },
                    )
                    HorizontalDivider(thickness = 1.dp, color = DividerLine)
                }
            }
        }
    }

    pendingDelete?.let { target ->
        // The swipe threshold is a quarter of the row width, so a stray
        // horizontal drag on a list you meant to scroll used to wipe a server
        // outright — a bookmark carries a nickname and a password nobody wants
        // to retype. Deleting is the one destructive action on this screen, so
        // it is the one that asks.
        AlertDialog(
            containerColor = SurfaceRaised,
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete server?", color = Bone) },
            text = { Text(target.label.ifBlank { target.address }, color = BoneMuted) },
            confirmButton = {
                TextButton(onClick = { onDelete(target.id); pendingDelete = null }) {
                    Text("DELETE", color = Oxblood)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("CANCEL", color = BoneMuted) }
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
private fun EmptyState(modifier: Modifier) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Outlined.Dns,
            contentDescription = null,
            tint = BoneFaint,
            modifier = Modifier.size(64.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No servers saved yet — tap + to add one.",
            color = BoneMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
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
        backgroundContent = {
            val alignment = when (swipeState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.CenterStart
            }
            val (label, color) = when (swipeState.dismissDirection) {
                SwipeToDismissBoxValue.EndToStart -> "DEL" to Oxblood
                else -> "EDIT" to Mauve
            }
            Box(
                modifier = Modifier.fillMaxSize().background(SurfaceRaised),
                contentAlignment = alignment,
            ) {
                Box(
                    // 80dp action strip, per design §2.
                    modifier = Modifier.width(80.dp).fillMaxHeight(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
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
            .height(72.dp)
            .background(Bg)
            .clickable { onConnect() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                bookmark.label.ifBlank { bookmark.address },
                color = Bone,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                "${bookmark.address}:${bookmark.port} · ${bookmark.nickname}",
                color = BoneFaint,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (connectedThisSession) Sage else DotStale),
        )
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
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = { SheetDragHandle() },
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text(
                if (isNew) "Add server" else "Edit server",
                style = MaterialTheme.typography.titleMedium,
                color = Bone,
            )
            Spacer(modifier = Modifier.height(16.dp))
            PlntTextField("Label", label, { label = it }, placeholder = "Home")
            Spacer(modifier = Modifier.height(12.dp))
            PlntTextField("Address", address, { address = it }, placeholder = "teamspeak.example.com")
            Spacer(modifier = Modifier.height(12.dp))
            PlntTextField(
                "Port",
                port,
                { port = it.filter(Char::isDigit) },
                keyboardType = KeyboardType.Number,
            )
            Spacer(modifier = Modifier.height(12.dp))
            PlntTextField(
                "Nickname",
                nickname,
                { nickname = it },
                trailing = {
                    IconButton(
                        onClick = { nickname = NicknameGenerator.random() },
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            Icons.Filled.Refresh,
                            contentDescription = "Generate nickname",
                            tint = Bone,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
            PlntTextField(
                "Server password",
                password,
                { password = it },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PlntPasswordMask,
                trailing = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Bone,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
            )

            Spacer(modifier = Modifier.height(24.dp))
            val canSave = address.isNotBlank()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onDismiss() }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Text("CANCEL", color = BoneMuted, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = canSave) {
                            onSave(
                                initial.copy(
                                    label = label,
                                    address = address.trim(),
                                    port = port.toIntOrNull() ?: 9987,
                                    nickname = nickname,
                                    serverPassword = password.ifBlank { null },
                                ),
                            )
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Text(
                        "SAVE",
                        // Disabled until address is non-empty, at 40% opacity.
                        color = if (canSave) Gold else Gold.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
