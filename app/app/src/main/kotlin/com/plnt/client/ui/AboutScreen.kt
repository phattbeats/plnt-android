package com.plnt.client.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plnt.client.R
import com.plnt.client.core.CoreBridge
import com.plnt.client.ui.theme.Bg
import com.plnt.client.ui.theme.PlntBrand
import com.plnt.client.ui.theme.TextFaint
import com.plnt.client.ui.theme.TextMuted
import com.plnt.client.ui.theme.TextNormal

/**
 * The rest of the app wears TeamSpeak's colours; this screen is where the
 * project's own name, plant mark and palette live (Brandon, PHA-3072,
 * 2026-10-10). It deliberately breaks from the blue theme so it reads as a
 * maker's mark rather than part of the product chrome.
 */
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Bg,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Bg),
                title = { Text("About", style = MaterialTheme.typography.titleLarge, color = TextNormal) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextNormal)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(PlntBrand.Soil)
                    .border(1.dp, PlntBrand.Sage.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(vertical = 28.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .border(2.dp, PlntBrand.Gold, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(R.drawable.plnt_mark),
                        contentDescription = "PLNT plant mark",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(108.dp).clip(CircleShape),
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    "PLNT",
                    color = PlntBrand.Bone,
                    fontFamily = FontFamily.Serif,
                    fontSize = 34.sp,
                    letterSpacing = 6.sp,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "A TeamSpeak client for phones: voice, chat, and screen shares.",
                    color = PlntBrand.Bone.copy(alpha = 0.75f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(PlntBrand.Bone, PlntBrand.Sage, PlntBrand.Mauve, PlntBrand.Oxblood, PlntBrand.Gold).forEach {
                        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(it))
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text("Made by PHATT TECH", color = PlntBrand.Gold, style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Built on tsclientlib (ReSpeak) and libopus.",
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "TeamSpeak and the TeamSpeak logo are trademarks of TeamSpeak Systems GmbH. " +
                    "This is an independent client, not made or endorsed by TeamSpeak.",
                color = TextFaint,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            // core_version() is the one string only the Rust core can produce,
            // so a broken JNI/UniFFI link is visible here without a debugger.
            Text(
                remember { CoreBridge.coreVersion() },
                color = TextFaint,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}
