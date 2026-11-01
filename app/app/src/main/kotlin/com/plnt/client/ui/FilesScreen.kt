package com.plnt.client.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import com.plnt.client.files.RemoteFile
import com.plnt.client.model.FilesUiState

/** PHA-4864: channel file browser — browse, upload, download, new folder, delete. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesScreen(
    state: FilesUiState,
    onOpenDir: (String) -> Unit,
    onUp: () -> Unit,
    onBack: () -> Unit,
    onDownload: (RemoteFile) -> Unit,
    onUpload: (android.net.Uri) -> Unit,
    onNewFolder: (String) -> Unit,
    onDelete: (String) -> Unit,
    onCancel: () -> Unit,
) {
    var newFolder by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf<RemoteFile?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { it?.let(onUpload) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Files  ${state.dir}") },
            navigationIcon = { IconButton(onClick = { if (state.dir == "/") onBack() else onUp() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
            actions = {
                IconButton(onClick = { newFolder = "" }, enabled = !state.busy) { Icon(Icons.Filled.Add, "New folder") }
                IconButton(onClick = { picker.launch(arrayOf("*/*")) }, enabled = !state.busy) { Icon(Icons.Filled.Upload, "Upload") }
            },
        )
    }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            state.progress?.let {
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(progress = { it }, modifier = Modifier.weight(1f))
                    TextButton(onClick = onCancel) { Text("Cancel") }
                }
            } ?: if (state.busy) LinearProgressIndicator(Modifier.fillMaxWidth()) else Unit
            (state.error ?: state.notice)?.let {
                Text(it, Modifier.padding(12.dp),
                    color = if (state.error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            }
            if (state.entries.isEmpty() && !state.busy && state.error == null) {
                Text("No files in this channel yet.", Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            LazyColumn(Modifier.fillMaxSize()) {
                items(state.entries, key = { it.name }) { f ->
                    Row(
                        Modifier.fillMaxWidth().clickable(enabled = !state.busy) { if (f.isDir) onOpenDir(f.name) else onDownload(f) }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(if (f.isDir) Icons.Filled.Folder else Icons.Filled.InsertDriveFile, null)
                        Column(Modifier.weight(1f)) {
                            Text(f.name)
                            if (!f.isDir) Text(humanSize(f.size), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (!f.isDir) IconButton(onClick = { onDownload(f) }, enabled = !state.busy) { Icon(Icons.Filled.Download, "Download ${f.name}") }
                        IconButton(onClick = { confirmDelete = f }, enabled = !state.busy) { Icon(Icons.Filled.Delete, "Delete ${f.name}") }
                    }
                }
            }
        }
    }

    newFolder?.let { name ->
        AlertDialog(
            onDismissRequest = { newFolder = null }, title = { Text("New folder") },
            text = { OutlinedTextField(name, { newFolder = it }, singleLine = true, label = { Text("Name") }) },
            confirmButton = { TextButton(onClick = { onNewFolder(name.trim()); newFolder = null }, enabled = name.isNotBlank()) { Text("Create") } },
            dismissButton = { TextButton(onClick = { newFolder = null }) { Text("Cancel") } },
        )
    }
    confirmDelete?.let { f ->
        AlertDialog(
            onDismissRequest = { confirmDelete = null }, title = { Text("Delete ${f.name}?") },
            text = { Text("This removes it for everyone in the channel.") },
            confirmButton = { TextButton(onClick = { onDelete(f.name); confirmDelete = null }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { confirmDelete = null }) { Text("Cancel") } },
        )
    }
}

internal fun humanSize(b: Long): String = when {
    b < 1024 -> "$b B"
    b < 1024 * 1024 -> "%.1f KB".format(b / 1024.0)
    b < 1024L * 1024 * 1024 -> "%.1f MB".format(b / 1048576.0)
    else -> "%.2f GB".format(b / 1073741824.0)
}
