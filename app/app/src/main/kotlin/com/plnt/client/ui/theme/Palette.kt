package com.plnt.client.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Palette (PHA-3072 rebrand, 2026-10-10): TeamSpeak blue on Discord-style
// greys. The greys step up in lightness the way Discord's mobile app layers
// them — window, list, card, pressed — so depth reads without borders.
val Bg = Color(0xFF1A1B1E)
val SurfaceDark = Color(0xFF232428)
val SurfaceRaised = Color(0xFF2B2D31)
val SurfaceHigh = Color(0xFF35373C)
val DividerLine = Color(0xFF3F4147)
val DividerFaint = Color(0xFF2B2D31)
val TextNormal = Color(0xFFF2F3F5)
val TextMuted = Color(0xFFB5BAC1)
val TextFaint = Color(0xFF80848E)

/**
 * TeamSpeak's brand blue (#4B69B6, per Simple Icons / teamspeak.com), lifted
 * one step so it holds ~4:1 both as a fill under white text and as text on
 * [Bg]. [BrandDeep] is the untouched brand value, for the launcher tile.
 */
val Brand = Color(0xFF5A78C8)
val BrandDeep = Color(0xFF4B69B6)
val OnBrand = Color(0xFFFFFFFF)

// Discord's status colours: speaking/online green, danger red, warning amber.
val Speaking = Color(0xFF23A55A)
val Danger = Color(0xFFF23F43)
val Warning = Color(0xFFF0B232)
val Accent = Color(0xFF949CF7)

// Row-lift backgrounds for talk state.
val RowTalking = Color(0xFF1F2A23)
val RowYouTalking = Color(0xFF232A3A)

// Bookmark trailing dot: green = connected during this session, this = never/older.
val DotStale = Color(0xFF4E5058)

/** Avatar tints, picked by name hash so a person keeps their colour between sessions. */
val AvatarTints = listOf(
    Color(0xFF5865F2), // blurple
    Color(0xFF3BA55C), // green
    Color(0xFFED4245), // red
    Color(0xFFC27C0E), // amber
    Color(0xFFEB459E), // fuchsia
    Color(0xFF4B69B6), // teamspeak
)

/** PLNT's own palette, now used only on the About screen. */
object PlntBrand {
    val Bone = Color(0xFFE6DCC8)
    val Sage = Color(0xFF8A9A7B)
    val Mauve = Color(0xFFA3788A)
    val Oxblood = Color(0xFF7A2E2E)
    val Gold = Color(0xFFC9A24A)
    val Soil = Color(0xFF0E0E0D)
}

private val PlntColorScheme = darkColorScheme(
    background = Bg,
    onBackground = TextNormal,
    surface = SurfaceDark,
    onSurface = TextNormal,
    surfaceVariant = SurfaceRaised,
    onSurfaceVariant = TextMuted,
    surfaceContainer = SurfaceRaised,
    surfaceContainerHigh = SurfaceHigh,
    primary = Brand,
    onPrimary = OnBrand,
    primaryContainer = Brand.copy(alpha = 0.18f),
    onPrimaryContainer = Brand,
    secondary = Speaking,
    onSecondary = OnBrand,
    secondaryContainer = Speaking.copy(alpha = 0.18f),
    onSecondaryContainer = Speaking,
    tertiary = Accent,
    onTertiary = Bg,
    error = Danger,
    onError = OnBrand,
    errorContainer = Danger.copy(alpha = 0.22f),
    onErrorContainer = Color(0xFFFFB3B5),
    outline = TextFaint,
    outlineVariant = DividerLine,
)

/**
 * One sans family throughout, with weight doing the hierarchy work — the same
 * approach Discord takes, and legible in a car mount at these sizes.
 */
private val PlntTypography = Typography(
    headlineSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 26.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.3).sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, letterSpacing = (-0.2).sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 15.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.2.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.3.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.6.sp),
)

private val PlntShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun PlntTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PlntColorScheme, typography = PlntTypography, shapes = PlntShapes, content = content)
}
