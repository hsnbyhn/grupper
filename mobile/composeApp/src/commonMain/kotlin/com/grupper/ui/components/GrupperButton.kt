package com.grupper.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.grupper.ui.theme.GrupperShapes
import com.grupper.ui.theme.GrupperSpacing

/**
 * Grupper Button Components
 * Based on Designer Agent's component specs (DESIGN-002)
 *
 * Variants:
 * - Primary: Filled with primary color, for main CTAs
 * - Secondary: Outlined, for alternative actions
 * - Text: Minimal, for tertiary actions
 * - Icon: Compact icon-only button
 */

@Composable
fun GrupperPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(GrupperSpacing.ButtonHeight),
        enabled = enabled && !isLoading,
        shape = GrupperShapes.Medium,
        contentPadding = PaddingValues(
            horizontal = GrupperSpacing.Default,
            vertical = GrupperSpacing.Medium
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(GrupperSpacing.IconDefault)
                )
                Spacer(modifier = Modifier.width(GrupperSpacing.Small))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun GrupperSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(GrupperSpacing.ButtonHeight),
        enabled = enabled && !isLoading,
        shape = GrupperShapes.Medium,
        contentPadding = PaddingValues(
            horizontal = GrupperSpacing.Default,
            vertical = GrupperSpacing.Medium
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
        } else {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(GrupperSpacing.IconDefault)
                )
                Spacer(modifier = Modifier.width(GrupperSpacing.Small))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun GrupperTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = PaddingValues(
            horizontal = GrupperSpacing.Medium,
            vertical = GrupperSpacing.Small
        )
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(GrupperSpacing.IconDefault)
            )
            Spacer(modifier = Modifier.width(GrupperSpacing.Small))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun GrupperIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(GrupperSpacing.MinTouchTarget),
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = tint
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(GrupperSpacing.IconDefault),
            tint = if (enabled) tint else tint.copy(alpha = 0.38f)
        )
    }
}
