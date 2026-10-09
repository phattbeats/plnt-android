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

// Palette: #3076's warm dark set, kept. The background is #0e0e0d rather than
// pure black on purpose — oxblood and sage stop reading as colour against #000.
val Bg = Color(0xFF0E0E0D)
val SurfaceDark = Color(0xFF161513)
val SurfaceRaised = Color(0xFF1E1D1A)
val SurfaceHigh = Color(0xFF262521)
val DividerLine = Color(0xFF2A2925)
val DividerFaint = Color(0xFF1C1B18)
val Bone = Color(0xFFEDE4D3)
val BoneMuted = Color(0xFF9A978A)
val BoneFaint = Color(0xFF66645A)
val Sage = Color(0xFF9AB08A)
val Gold = Color(0xFFD9B35A)
val Oxblood = Color(0xFF9A3D3D)
val Mauve = Color(0xFFB48BA0)

// Row-lift backgrounds for talk state.
val RowTalking = Color(0xFF151A14)
val RowYouTalking = Color(0xFF1C1810)

// Bookmark trailing dot: sage = connected during this session, this = never/older.
val DotStale = Color(0xFF4A4840)

/** Avatar tints, picked by name hash so a person keeps their colour between sessions. */
val AvatarTints = listOf(
    Color(0xFF5B6B4E), // moss
    Color(0xFF6B5A3E), // umber
    Color(0xFF4E5B6B), // slate
    Color(0xFF6B4E5E), // plum
    Color(0xFF3E6B66), // teal
    Color(0xFF6B4E3E), // rust
)

private val PlntColorScheme = darkColorScheme(
    background = Bg,
    onBackground = Bone,
    surface = SurfaceDark,
    onSurface = Bone,
    surfaceVariant = SurfaceRaised,
    onSurfaceVariant = BoneMuted,
    surfaceContainer = SurfaceRaised,
    surfaceContainerHigh = SurfaceHigh,
    primary = Gold,
    onPrimary = Bg,
    primaryContainer = Gold.copy(alpha = 0.18f),
    onPrimaryContainer = Gold,
    secondary = Sage,
    onSecondary = Bg,
    secondaryContainer = Sage.copy(alpha = 0.18f),
    onSecondaryContainer = Sage,
    tertiary = Mauve,
    onTertiary = Bg,
    error = Oxblood,
    onError = Bone,
    errorContainer = Oxblood.copy(alpha = 0.22f),
    onErrorContainer = Color(0xFFE8A3A3),
    outline = BoneFaint,
    outlineVariant = DividerLine,
)

/**
 * One sans family throughout, with weight doing the hierarchy work. The
 * original serif headers read as dated on Android's fallback serif; a
 * semibold sans title at 22sp reads as a product, and stays legible in a car
 * mount at the same sizes.
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
