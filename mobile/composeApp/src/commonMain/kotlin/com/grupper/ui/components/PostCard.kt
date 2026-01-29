package com.grupper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.grupper.data.model.Post
import com.grupper.ui.theme.GrupperIcons
import com.grupper.ui.theme.GrupperSpacing
import kotlin.math.abs

/**
 * Post Card Component
 * Based on Designer Agent's Group Detail screen design (DESIGN-006)
 *
 * Layout:
 * - Tag chip at top
 * - Post title (Title Medium, max 2 lines)
 * - Content preview (Body Medium, max 3 lines)
 * - Metadata row (author, comment count, timestamp)
 */
@Composable
fun PostCard(
    post: Post,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GrupperCard(
        onClick = onClick,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(GrupperSpacing.Small)
        ) {
            // Tag chip and timestamp row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tag chip
                if (post.tag != null) {
                    TagChip(
                        name = post.tag.name,
                        color = Color(parseColor(post.tag.color)),
                        isSelected = false,
                        onClick = {} // Not interactive in card
                    )
                }

                // Timestamp
                Text(
                    text = formatTimeAgo(post.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Post title
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Content preview
            if (post.content.isNotBlank()) {
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(GrupperSpacing.XSmall))

            // Metadata row
            Row(
                horizontalArrangement = Arrangement.spacedBy(GrupperSpacing.Medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author
                Text(
                    text = "By ${post.authorName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Comment count
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
                        text = "${post.commentCount} ${if (post.commentCount == 1) "comment" else "comments"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Parse color hex string to Color
 * Handles both #RRGGBB and RRGGBB formats
 */
private fun parseColor(colorHex: String): Long {
    val hex = colorHex.removePrefix("#")
    return ("FF" + hex).toLong(16)
}

/**
 * Format timestamp to relative time (e.g., "2h ago", "3d ago")
 */
private fun formatTimeAgo(timestamp: String): String {
    // This is a simplified version
    // In production, use kotlinx.datetime for proper parsing

    // For now, return simplified relative time
    // TODO: Implement proper time parsing with kotlinx.datetime

    // Mock implementation - replace with actual date parsing
    val mockHoursAgo = abs(timestamp.hashCode() % 72)

    return when {
        mockHoursAgo < 1 -> "Just now"
        mockHoursAgo < 24 -> "${mockHoursAgo}h ago"
        mockHoursAgo < 168 -> "${mockHoursAgo / 24}d ago"
        else -> "${mockHoursAgo / 168}w ago"
    }
}
