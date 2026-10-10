package com.plnt.client.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.plnt.client.model.ServerLink
import com.plnt.client.ui.theme.TextNormal
import com.plnt.client.ui.theme.TextFaint
import com.plnt.client.ui.theme.TextMuted
import com.plnt.client.ui.theme.Brand
import com.plnt.client.ui.theme.SurfaceDark
import com.plnt.client.ui.theme.SurfaceHigh
import com.plnt.client.ui.theme.SurfaceRaised
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * PHA-4108, incoming side: a `ts3server://` link opened PLNT. Show where it
 * leads, let the user keep it, and connect. Always offered before any network
 * call — a tapped link should never silently dial out.
 */
@Composable
fun InviteSheet(
    invite: ServerLink,
    knownServer: Boolean,
    onJoin: (save: Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    // Default to saving a new server, but not re-saving one already in the list.
    var save by remember(invite, knownServer) { mutableStateOf(!knownServer) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { SheetDragHandle() },
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
            Text(
                if (knownServer) "Connect to this server?" else "You've been invited",
                style = MaterialTheme.typography.titleLarge,
                color = TextNormal,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceHigh),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Dns, contentDescription = null, tint = Brand, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        invite.label ?: invite.address,
                        color = TextNormal,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        "${invite.address}:${invite.port}",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    invite.channel?.let {
                        Text("channel: $it", color = TextFaint, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(if (save) Brand.copy(alpha = 0.08f) else SurfaceRaised)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Checkbox(
                    checked = save,
                    onCheckedChange = { save = it },
                    colors = CheckboxDefaults.colors(checkedColor = Brand, uncheckedColor = TextMuted),
                )
                Text(
                    if (knownServer) "Already in your servers" else "Save to my servers",
                    color = if (knownServer) TextFaint else TextMuted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton("Not now", onClick = onDismiss, modifier = Modifier.weight(1f), color = TextMuted)
                PrimaryButton(
                    "Join",
                    icon = Icons.Filled.Login,
                    modifier = Modifier.weight(1f),
                    onClick = { onJoin(save) },
                )
            }
        }
    }
}

/**
 * PHA-4108, outgoing side: a sheet that shows a server's invite link as text
 * and a QR code, with a system share button. The nickname is deliberately
 * never part of the link ([ServerLink.toUri]) — everyone chooses their own.
 */
@Composable
fun ShareInviteSheet(
    link: ServerLink,
    onDismiss: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val uri = remember(link) { link.toUri() }
    val qr = remember(uri) { encodeQr(uri, 640) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { SheetDragHandle() },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Invite to ${link.label ?: link.address}", style = MaterialTheme.typography.titleLarge, color = TextNormal)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "They scan this or open the link in TeamSpeak, and the server is filled in for them.",
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (qr != null) {
                // White quiet-zone card: a QR must have light margins to scan.
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        .background(androidx.compose.ui.graphics.Color.White)
                        .padding(16.dp),
                ) {
                    Image(
                        painter = BitmapPainter(qr.asImageBitmap()),
                        contentDescription = "Invite QR code",
                        modifier = Modifier.size(220.dp),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Text(uri, color = TextFaint, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton("Close", onClick = onDismiss, modifier = Modifier.weight(1f), color = TextMuted)
                PrimaryButton("Share link", modifier = Modifier.weight(1f), onClick = { shareText(context, uri) })
            }
        }
    }
}

/** Fires the system share chooser with the invite URI as plain text. */
private fun shareText(context: Context, text: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_SUBJECT, "Join me on TeamSpeak")
    }
    context.startActivity(Intent.createChooser(send, "Share invite"))
}

/** QR matrix → black-on-white [Bitmap]. Null if the string somehow won't encode. */
private fun encodeQr(content: String, size: Int): Bitmap? = runCatching {
    val hints = mapOf(
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
        EncodeHintType.MARGIN to 1,
    )
    val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp.setPixel(x, y, if (matrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
        }
    }
    bmp
}.getOrNull()
