package com.grupper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.grupper.ui.theme.GrupperShapes
import com.grupper.ui.theme.GrupperSpacing

/**
 * Tag Chip Component
 * Based on Designer Agent's component specs (DESIGN-002)
 *
 * Features:
 * - Small border radius (8dp)
 * - Custom background colors for visual categorization
 * - High contrast text for readability
 * - Tappable for filtering
 */

@Composable
fun TagChip(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = if (isSelected) {
        color
    } else {
        color.copy(alpha = 0.15f)
    }

    val textColor = if (isSelected) {
        // Calculate contrast color for selected state
        if (color.luminance() > 0.5f) Color.Black else Color.White
    } else {
        color
    }

    val borderColor = if (isSelected) {
        Color.Transparent
    } else {
        color.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .clip(GrupperShapes.Small)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = GrupperShapes.Small
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = GrupperSpacing.Medium,
                vertical = GrupperSpacing.XSmall
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}

@Composable
fun TagChipSmall(
    name: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(GrupperShapes.Small)
            .background(color.copy(alpha = 0.15f))
            .padding(
                horizontal = GrupperSpacing.Small,
                vertical = GrupperSpacing.XXSmall
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

/**
 * Calculate relative luminance of a color
 * Used to determine if text should be light or dark
 */
private fun Color.luminance(): Float {
    val r = red
    val g = green
    val b = blue
    return 0.299f * r + 0.587f * g + 0.114f * b
}
