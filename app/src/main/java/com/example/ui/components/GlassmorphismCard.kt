package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink

@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Color(0x1AFFFFFF),
    glowGradient: Boolean = false,
    cornerShape: RoundedCornerShape = RoundedCornerShape(20.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val borderModifier = if (glowGradient) {
        Modifier.border(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(NeonPink, NeonCyan)
            ),
            shape = cornerShape
        )
    } else {
        Modifier.border(width = 1.dp, color = borderColor, shape = cornerShape)
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable { onClick() }
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cornerShape)
            .background(Color(0x08FFFFFF))
            .then(borderModifier)
            .then(clickableModifier)
            .padding(14.dp)
    ) {
        content()
    }
}

