package com.plnt.client.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// PHA-3076 "Colors" table, verbatim. The background is #0e0e0d rather than pure
// black on purpose — oxblood and sage stop reading as colour against #000.
val Bg = Color(0xFF0E0E0D)
val SurfaceDark = Color(0xFF161513)
val SurfaceRaised = Color(0xFF1A1917)
val DividerLine = Color(0xFF232320)
val DividerFaint = Color(0xFF1A1917)
val Bone = Color(0xFFE6DCC8)
val BoneMuted = Color(0xFF8A877A)
val BoneFaint = Color(0xFF5C5A4F)
val Sage = Color(0xFF8A9A7B)
val Gold = Color(0xFFC9A24A)
val Oxblood = Color(0xFF7A2E2E)
val Mauve = Color(0xFFA3788A)

// Row-lift backgrounds from the state → colour table.
val RowTalking = Color(0xFF141813)
val RowYouTalking = Color(0xFF1A1710)

// Bookmark trailing dot: sage = connected during this session, this = never/older.
val DotStale = Color(0xFF4A4840)

private val PlntColorScheme = darkColorScheme(
    background = Bg,
    onBackground = Bone,
    surface = SurfaceDark,
    onSurface = Bone,
    surfaceVariant = SurfaceRaised,
    onSurfaceVariant = BoneMuted,
    primary = Gold,
    onPrimary = Bg,
    secondary = Sage,
    onSecondary = Bg,
    tertiary = Mauve,
    onTertiary = Bg,
    error = Oxblood,
    onError = Bone,
    outline = BoneMuted,
    outlineVariant = DividerLine,
)

/**
 * PHA-3076 typography: Georgia for headers only, Roboto for everything else.
 * Georgia ships as Android's fallback serif, so this asks for [FontFamily.Serif]
 * rather than bundling a font file — and it is deliberately never used below
 * 16sp, where the design found it stops being legible in a car mount.
 */
private val PlntTypography = Typography(
    titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 20.sp, fontWeight = FontWeight.Normal),
    titleMedium = TextStyle(fontFamily = FontFamily.Serif, fontSize = 18.sp, fontWeight = FontWeight.Normal),
    titleSmall = TextStyle(fontFamily = FontFamily.Serif, fontSize = 16.sp, fontWeight = FontWeight.Normal),
    bodyLarge = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.Default, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = FontFamily.Default, fontSize = 12.sp),
    // Section headers / captions: 11sp all-caps, tracked out.
    labelSmall = TextStyle(fontFamily = FontFamily.Default, fontSize = 11.sp, letterSpacing = 0.5.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Default, fontSize = 12.sp, letterSpacing = 0.5.sp),
)

@Composable
fun PlntTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PlntColorScheme, typography = PlntTypography, content = content)
}
