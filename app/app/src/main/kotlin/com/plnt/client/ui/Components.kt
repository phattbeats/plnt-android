package com.plnt.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.plnt.client.ui.theme.AvatarTints
import com.plnt.client.ui.theme.Bg
import com.plnt.client.ui.theme.Bone
import com.plnt.client.ui.theme.BoneFaint
import com.plnt.client.ui.theme.BoneMuted
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.SurfaceDark
import com.plnt.client.ui.theme.SurfaceRaised

/** Tracked-out caption used to head a group of rows. */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        color = BoneMuted,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier.padding(start = 4.dp, bottom = 8.dp),
    )
}

/** Grouped surface: rows live inside one of these, never loose on the background. */
@Composable
fun PlntCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, DividerLine, RoundedCornerShape(16.dp)),
    ) {
        content()
    }
}

/** Small coloured status dot, 8dp. */
@Composable
fun StatusDot(color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(8.dp).clip(CircleShape).background(color))
}

/** Compact chip: tinted background, optional leading icon, 11sp label. */
@Composable
fun Pill(text: String, color: Color, icon: ImageVector? = null, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(text, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

/** Filled gold call-to-action, 48dp. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) Gold else Gold.copy(alpha = 0.35f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = Bg, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, color = Bg, style = MaterialTheme.typography.labelLarge)
    }
}

/** Outlined companion to [PrimaryButton]; [color] tints border, icon and label. */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Bone,
    icon: ImageVector? = null,
) {
    val tint = if (enabled) color else color.copy(alpha = 0.4f)
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, tint.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, color = tint, style = MaterialTheme.typography.labelLarge)
    }
}

/** Text-only action for sheet footers. */
@Composable
fun TextAction(text: String, onClick: () -> Unit, enabled: Boolean = true, color: Color = Gold) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(text, color = if (enabled) color else color.copy(alpha = 0.4f), style = MaterialTheme.typography.labelLarge)
    }
}

/** Deterministic avatar tint for a display name. */
fun avatarTint(name: String): Color = AvatarTints[(name.hashCode() and 0x7fffffff) % AvatarTints.size]

/** Initial-letter avatar on a name-derived tint. */
@Composable
fun Avatar(name: String, size: Dp = 36.dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(size).clip(CircleShape).background(avatarTint(name)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            name.firstOrNull()?.uppercase() ?: "?",
            color = Bone,
            style = if (size >= 40.dp) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall,
        )
    }
}

/**
 * Text field: 48dp, 12dp corners, label above. Material's OutlinedTextField
 * floats its label inside the border, which fights the caption-above rhythm
 * every other screen uses.
 */
@Composable
fun PlntTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(label, modifier = Modifier.padding(bottom = 2.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceRaised)
                .border(1.dp, DividerLine, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(placeholder, color = BoneFaint, style = MaterialTheme.typography.bodyLarge)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.merge(MaterialTheme.typography.bodyLarge).copy(color = Bone),
                    cursorBrush = SolidColor(Gold),
                    visualTransformation = visualTransformation,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            trailing?.invoke()
        }
    }
}

/** Masking transformation, kept next to the field it belongs to. */
val PlntPasswordMask: VisualTransformation = PasswordVisualTransformation()

/** Bottom-sheet drag handle: 36×4dp, centred. */
@Composable
fun SheetDragHandle() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .background(BoneFaint, RoundedCornerShape(2.dp)),
        )
    }
}
