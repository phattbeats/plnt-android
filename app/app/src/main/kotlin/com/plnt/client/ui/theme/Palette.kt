package com.plnt.client.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// PHA-3076 palette. Dark, quiet, PLNT. Never used in a light theme.
val Bone = Color(0xFFE6DCC8)
val Sage = Color(0xFF8A9A7B)
val Mauve = Color(0xFFA3788A)
val Oxblood = Color(0xFF7A2E2E)
val Gold = Color(0xFFC9A24A)
val NearBlack = Color(0xFF121110)
val NearBlackElevated = Color(0xFF1B1917)

private val PlntColorScheme = darkColorScheme(
    background = NearBlack,
    surface = NearBlackElevated,
    primary = Gold,
    onPrimary = NearBlack,
    secondary = Sage,
    onSecondary = NearBlack,
    tertiary = Mauve,
    error = Oxblood,
    onBackground = Bone,
    onSurface = Bone,
)

@Composable
fun PlntTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PlntColorScheme, content = content)
}
