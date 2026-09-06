package com.plnt.client.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.plnt.client.ui.theme.Bone
import com.plnt.client.ui.theme.BoneFaint
import com.plnt.client.ui.theme.DividerLine
import com.plnt.client.ui.theme.Gold
import com.plnt.client.ui.theme.Mauve
import com.plnt.client.ui.theme.SurfaceRaised

/** 11sp all-caps mauve section header (PHA-3076 §5). */
@Composable
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(),
        color = Mauve,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier.padding(bottom = 8.dp),
    )
}

/**
 * Text field per the spec table: 44dp tall, 8dp corners, 1.5dp `divider`
 * outline, and an 11sp mauve label *above* the box rather than a Material
 * floating/inline label. Material3's `OutlinedTextField` can't be squeezed to
 * 44dp or made to put its label outside the border, so this is a
 * `BasicTextField` with the spec'd decoration around it.
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
        SectionHeader(label, modifier = Modifier.padding(bottom = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .border(1.5.dp, DividerLine, RoundedCornerShape(8.dp))
                .background(SurfaceRaised, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Text(placeholder, color = BoneFaint, style = MaterialTheme.typography.bodyMedium)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.merge(MaterialTheme.typography.bodyMedium).copy(color = Bone),
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

/** Bottom-sheet drag handle: 40×4dp, centred (spec table). */
@Composable
fun SheetDragHandle() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 4.dp)
                .background(BoneFaint, RoundedCornerShape(2.dp)),
        )
    }
}
