package com.grupper.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.grupper.data.model.Group
import com.grupper.ui.theme.GrupperIcons
import com.grupper.ui.theme.GrupperSpacing

/**
 * Group Card Component
 * Based on Designer Agent's Groups List screen design (DESIGN-004)
 *
 * Layout:
 * - Circular image placeholder (64dp)
 * - Group name (Title Medium, max 1 line)
 * - Description (Body Medium, max 2 lines)
 * - Metadata row (member count, post count)
 */
@Composable
fun GroupCard(
    group: Group,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GrupperCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Default)
        ) {
            // Group image placeholder
            GroupAvatar(
                imageUrl = group.imageUrl,
                groupName = group.name,
                modifier = Modifier.size(64.dp)
            )

            // Group info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Group name
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(GrupperSpacing.XSmall))

                // Description
                Text(
                    text = group.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(GrupperSpacing.Small))

                // Metadata row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Member count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.XSmall)
                    ) {
                        Icon(
                            imageVector = GrupperIcons.Person,
                            contentDescription = null,
                            modifier = Modifier.size(GrupperSpacing.IconSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatCount(group.memberCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Post count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.XSmall)
                    ) {
                        Icon(
                            imageVector = GrupperIcons.Message,
                            contentDescription = null,
                            modifier = Modifier.size(GrupperSpacing.IconSmall),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${formatCount(group.postCount)} posts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Group Avatar - circular image with fallback to initials
 */
@Composable
fun GroupAvatar(
    imageUrl: String?,
    groupName: String,
    modifier: Modifier = Modifier
) {
    // For now, show initials placeholder
    // TODO: Add Coil image loading when backend provides images
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = getInitials(groupName),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Get initials from group name (up to 2 characters)
 */
private fun getInitials(name: String): String {
    return name.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "G" }
}

/**
 * Format count with K/M suffixes for large numbers
 */
private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "${count / 1_000_000}M"
        count >= 1_000 -> "${count / 1_000}K"
        else -> count.toString()
    }
}
