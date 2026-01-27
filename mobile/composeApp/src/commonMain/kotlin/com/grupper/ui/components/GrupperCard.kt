package com.grupper.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.grupper.ui.theme.GrupperElevation
import com.grupper.ui.theme.GrupperShapes
import com.grupper.ui.theme.GrupperSpacing

/**
 * Grupper Card Component
 * Based on Designer Agent's component specs (DESIGN-002)
 *
 * Features:
 * - Medium border radius (12dp)
 * - Subtle elevation (Level 1)
 * - Consistent padding
 * - Ripple effect on tap
 */

@Composable
fun GrupperCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Dp = GrupperElevation.Level1,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = GrupperShapes.Medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation,
                pressedElevation = GrupperElevation.Level0
            )
        ) {
            Column(
                modifier = Modifier.padding(GrupperSpacing.Default),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = GrupperShapes.Medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation
            )
        ) {
            Column(
                modifier = Modifier.padding(GrupperSpacing.Default),
                content = content
            )
        }
    }
}

@Composable
fun GrupperCardNoPadding(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Dp = GrupperElevation.Level1,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = GrupperShapes.Medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation,
                pressedElevation = GrupperElevation.Level0
            ),
            content = content
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = GrupperShapes.Medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation
            ),
            content = content
        )
    }
}
