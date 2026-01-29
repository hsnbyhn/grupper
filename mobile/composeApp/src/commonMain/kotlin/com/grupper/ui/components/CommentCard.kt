package com.grupper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grupper.data.model.Comment
import com.grupper.ui.theme.GrupperIcons
import com.grupper.ui.theme.GrupperSpacing

/**
 * Comment card component with nested threading support
 * Displays author, content, timestamp, and reply button
 * Recursively renders nested replies with indentation
 *
 * Based on DESIGN-010 specifications
 */
@Composable
fun CommentCard(
    comment: Comment,
    onReplyClick: (Comment) -> Unit,
    modifier: Modifier = Modifier,
    nestingLevel: Int = 0
) {
    val maxNestingLevel = 4 // Flatten comments after 4 levels
    val indentationDp = (nestingLevel * 16).dp // 16dp per level

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = indentationDp)
    ) {
        // Comment card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(GrupperSpacing.Medium)
        ) {
            // Header: Author + timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author name
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Timestamp
                Text(
                    text = formatTimestamp(comment.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(GrupperSpacing.Small))

            // Comment content
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(GrupperSpacing.Small))

            // Reply button
            GrupperTextButton(
                text = "Reply",
                onClick = { onReplyClick(comment) },
                leadingIcon = GrupperIcons.Reply,
                modifier = Modifier.height(32.dp)
            )
        }

        // Nested replies (recursive)
        if (comment.replies.isNotEmpty() && nestingLevel < maxNestingLevel) {
            Spacer(modifier = Modifier.height(GrupperSpacing.Small))
            comment.replies.forEach { reply ->
                CommentCard(
                    comment = reply,
                    onReplyClick = onReplyClick,
                    nestingLevel = nestingLevel + 1
                )
                Spacer(modifier = Modifier.height(GrupperSpacing.Small))
            }
        }
    }
}

/**
 * Format ISO timestamp to relative time
 * e.g., "2h ago", "5d ago", "Jan 15"
 */
private fun formatTimestamp(isoTimestamp: String): String {
    // TODO: Implement proper relative time formatting with kotlinx.datetime
    // For now, return simplified format
    return try {
        val parts = isoTimestamp.split("T")
        val date = parts[0]
        val dateParts = date.split("-")
        val month = when (dateParts[1]) {
            "01" -> "Jan"
            "02" -> "Feb"
            "03" -> "Mar"
            "04" -> "Apr"
            "05" -> "May"
            "06" -> "Jun"
            "07" -> "Jul"
            "08" -> "Aug"
            "09" -> "Sep"
            "10" -> "Oct"
            "11" -> "Nov"
            "12" -> "Dec"
            else -> dateParts[1]
        }
        "$month ${dateParts[2]}"
    } catch (e: Exception) {
        "Recently"
    }
}
