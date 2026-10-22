package com.plnt.client.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.plnt.client.account.*
import com.plnt.client.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Credentials and decrypted preview live only for this sheet; never rememberSaveable. */
@Composable
fun AccountSignInSheet(
    importAllowed: Boolean,
    onImport: suspend (List<AccountEntry>) -> Boolean,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context as? Activity
    DisposableEffect(activity) {
        val wasSecure = activity?.window?.attributes?.flags?.and(WindowManager.LayoutParams.FLAG_SECURE) != 0
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose { if (!wasSecure) activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var needsOtp by remember { mutableStateOf(false) }
    var preview by remember { mutableStateOf<AccountPreview?>(null) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var imported by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { if (!busy) onDismiss() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SurfaceDark,
        properties = ModalBottomSheetProperties(securePolicy = SecureFlagPolicy.SecureOn),
    ) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).imePadding()
            .padding(horizontal = 24.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Sign in with myTeamSpeak", color = TextNormal)
            Text("Download synced servers and identities, then choose what to save in Relay. Your account password and session are not saved. Nothing is uploaded to your account.", color = TextMuted)
            if (preview == null && !imported) {
                PlntTextField("Email", email, { if (!busy) email = it }, keyboardType = KeyboardType.Email)
                PlntTextField("Password", password, { if (!busy) password = it }, visualTransformation = PlntPasswordMask)
                if (needsOtp) PlntTextField("Verification code", otp, { if (!busy) otp = it }, keyboardType = KeyboardType.Number)
                PrimaryButton(if (busy) "Signing in…" else if (needsOtp) "Verify and download" else "Sign in and download",
                    enabled = !busy && email.isNotBlank() && password.isNotEmpty() && (!needsOtp || otp.isNotBlank()),
                    modifier = Modifier.fillMaxWidth(), onClick = {
                        busy = true
                        message = null
                        scope.launch {
                            try {
                                preview = withContext(Dispatchers.IO) {
                                    AccountClient().fetch(email, password, otp, AccountStore(context).deviceId())
                                }
                                password = ""; otp = ""
                                selected = emptySet()
                                if (preview?.entries?.isEmpty() == true) message = "No synced servers were returned. Nothing was imported."
                            } catch (e: kotlinx.coroutines.CancellationException) { throw e
                            } catch (e: AccountFailure) {
                                needsOtp = e.needsOtp
                                otp = ""
                                message = e.userMessage
                                if (!needsOtp) password = ""
                            } catch (e: Exception) {
                                password = ""; otp = ""
                                message = "Could not open the secure account store. Nothing was imported."
                            } finally { busy = false }
                        }
                    })
            }
            preview?.let { result ->
                Text("Choose servers to import. Their matching identities are saved securely without replacing your current identity. Existing imports of the same servers will be updated.", color = TextMuted)
                for (entry in result.entries) Row(Modifier.fillMaxWidth()) {
                    Checkbox(checked = entry.bookmark.id in selected, enabled = !busy,
                        onCheckedChange = { checked -> selected = if (checked) selected + entry.bookmark.id else selected - entry.bookmark.id })
                    Column(Modifier.weight(1f).padding(top = 8.dp)) {
                        Text(entry.bookmark.label.ifBlank { entry.bookmark.address }, color = TextNormal)
                        Text("${entry.bookmark.address}:${entry.bookmark.port} · ${entry.bookmark.nickname}", color = TextMuted)
                    }
                }
                PrimaryButton(if (busy) "Saving…" else "Import selected servers",
                    enabled = !busy && importAllowed && selected.isNotEmpty(), modifier = Modifier.fillMaxWidth(), onClick = {
                        busy = true
                        scope.launch {
                            try {
                                if (onImport(result.entries.filter { it.bookmark.id in selected })) {
                                    preview = null; selected = emptySet(); imported = true
                                    message = "Imported. Open Servers and connect to test your identity and permissions."
                                } else message = "Could not save the import. Disconnect and try again."
                            } finally { busy = false }
                        }
                    })
            }
            if (!importAllowed) Text("Disconnect before importing servers and identities.", color = TextMuted)
            message?.let { Text(it, color = TextNormal) }
            SecondaryButton(if (imported) "Done" else "Cancel", enabled = !busy, modifier = Modifier.fillMaxWidth(), onClick = onDismiss)
        }
    }
}
