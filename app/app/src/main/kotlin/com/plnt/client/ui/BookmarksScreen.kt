package com.plnt.client.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.plnt.client.model.Bookmark

/** PHA-3076 screen 1: bookmarks list with add/edit (address, port, nickname, server password). */
@Composable
fun BookmarksScreen(
    bookmarks: List<Bookmark>,
    onConnect: (Bookmark) -> Unit,
    onSave: (Bookmark) -> Unit,
    onDelete: (String) -> Unit,
    newId: () -> String,
    onOpenSettings: () -> Unit,
) {
    var editing by remember { mutableStateOf<Bookmark?>(null) }
    var showEditor by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PLNT") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editing = Bookmark(id = newId(), label = "", address = "", port = 9987, nickname = "")
                showEditor = true
            }) { Icon(Icons.Filled.Add, contentDescription = "Add bookmark") }
        },
    ) { padding ->
        if (bookmarks.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("No bookmarks yet.", style = MaterialTheme.typography.bodyLarge)
                Text("Tap + to add a server.", style = MaterialTheme.typography.bodySmall)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(bookmarks, key = { it.id }) { b ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(b.label.ifBlank { b.address }, style = MaterialTheme.typography.titleMedium)
                                Text("${b.address}:${b.port} as ${b.nickname}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { editing = b; showEditor = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = { onDelete(b.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Button(onClick = { onConnect(b) }) { Text("Connect") }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }

    val current = editing
    if (showEditor && current != null) {
        BookmarkEditorDialog(
            initial = current,
            onDismiss = { showEditor = false },
            onSave = { onSave(it); showEditor = false },
        )
    }
}

@Composable
private fun BookmarkEditorDialog(
    initial: Bookmark,
    onDismiss: () -> Unit,
    onSave: (Bookmark) -> Unit,
) {
    var label by remember { mutableStateOf(initial.label) }
    var address by remember { mutableStateOf(initial.address) }
    var port by remember { mutableStateOf(initial.port.toString()) }
    var nickname by remember { mutableStateOf(initial.nickname) }
    var password by remember { mutableStateOf(initial.serverPassword.orEmpty()) }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bookmark") },
        text = {
            Column {
                OutlinedTextField(label, { label = it }, label = { Text("Label") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(address, { address = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(port, { port = it.filter(Char::isDigit) }, label = { Text("Port") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(nickname, { nickname = it }, label = { Text("Nickname") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    password,
                    { password = it },
                    label = { Text("Server password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            )
                        }
                    },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    initial.copy(
                        label = label,
                        address = address,
                        port = port.toIntOrNull() ?: 9987,
                        nickname = nickname,
                        serverPassword = password.ifBlank { null },
                    )
                )
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
