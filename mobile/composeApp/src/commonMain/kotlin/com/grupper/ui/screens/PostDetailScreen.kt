package com.grupper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.grupper.data.model.Comment
import com.grupper.ui.components.*
import com.grupper.ui.theme.GrupperIcons
import com.grupper.ui.theme.GrupperShapes
import com.grupper.ui.theme.GrupperSpacing
import com.grupper.viewmodel.PostDetailViewModel

/**
 * Post Detail Screen
 * Shows full post content with image and threaded comments section
 *
 * Features:
 * - Full post display with tag, title, content, image
 * - Nested/threaded comments with indentation
 * - Reply functionality for each comment
 * - Add top-level comment input at bottom
 * - Pull-to-refresh
 * - Empty state when no comments
 *
 * Based on DESIGN-009 specifications
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = viewModel { PostDetailViewModel() }
) {
    val uiState = viewModel.uiState

    // Load post on first composition
    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    // State for reply dialog
    var replyingToComment by remember { mutableStateOf<Comment?>(null) }
    var showAddCommentDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.post?.title ?: "Post",
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = GrupperIcons.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState.post != null) {
                FloatingActionButton(
                    onClick = { showAddCommentDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = GrupperIcons.Add,
                        contentDescription = "Add comment"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingScreen(message = "Loading post...")
                }

                uiState.hasError -> {
                    ErrorView(
                        title = "Failed to load post",
                        message = uiState.error ?: "Unable to load post. Please try again.",
                        onRetry = { viewModel.loadPost(postId) }
                    )
                }

                uiState.post != null -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.refreshPost() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(GrupperSpacing.Medium)
                        ) {
                            // Post content section
                            item {
                                PostContentSection(post = uiState.post)
                            }

                            // Comments section divider
                            item {
                                Spacer(modifier = Modifier.height(GrupperSpacing.Large))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(GrupperSpacing.Medium))

                                Text(
                                    text = "Comments (${countAllComments(uiState.comments)})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(GrupperSpacing.Medium))
                            }

                            // Empty state
                            if (uiState.hasNoComments) {
                                item {
                                    EmptyStateView(
                                        title = "No comments yet",
                                        message = "Be the first to share your thoughts!",
                                        actionLabel = "Add Comment",
                                        onAction = { showAddCommentDialog = true }
                                    )
                                }
                            }

                            // Comments list (nested)
                            items(uiState.comments) { comment ->
                                CommentCard(
                                    comment = comment,
                                    onReplyClick = { replyingToComment = it }
                                )
                                Spacer(modifier = Modifier.height(GrupperSpacing.Medium))
                            }

                            // Bottom spacing for FAB
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Add comment dialog
    if (showAddCommentDialog) {
        AddCommentDialog(
            onDismiss = { showAddCommentDialog = false },
            onSubmit = { authorName, content ->
                viewModel.addComment(authorName, content)
                showAddCommentDialog = false
            },
            isSubmitting = uiState.isSubmittingComment
        )
    }

    // Reply dialog
    if (replyingToComment != null) {
        AddCommentDialog(
            title = "Reply to ${replyingToComment?.authorName}",
            onDismiss = { replyingToComment = null },
            onSubmit = { authorName, content ->
                viewModel.addReply(replyingToComment!!, authorName, content)
                replyingToComment = null
            },
            isSubmitting = uiState.isSubmittingComment
        )
    }
}

/**
 * Post content section with tag, title, content, and optional image
 */
@Composable
private fun PostContentSection(post: com.grupper.data.model.Post) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Tag and metadata row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tag chip
            post.tag?.let { tag ->
                TagChip(
                    name = tag.name,
                    color = Color(parseColor(tag.color)),
                    isSelected = false,
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.width(GrupperSpacing.Small))

            // Author and stats
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "by ${post.authorName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${post.commentCount} comments",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(GrupperSpacing.Medium))

        // Post title
        Text(
            text = post.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(GrupperSpacing.Medium))

        // Post content (full text)
        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Post image (if present)
        // TODO: Add image loading when backend provides images
        post.imageUrl?.let { imageUrl ->
            Spacer(modifier = Modifier.height(GrupperSpacing.Medium))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(GrupperShapes.Medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Image placeholder",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Dialog for adding a comment or reply
 */
@Composable
private fun AddCommentDialog(
    title: String = "Add Comment",
    onDismiss: () -> Unit,
    onSubmit: (authorName: String, content: String) -> Unit,
    isSubmitting: Boolean
) {
    var authorName by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                GrupperTextField(
                    value = authorName,
                    onValueChange = { authorName = it },
                    label = "Your name",
                    placeholder = "Anonymous",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(GrupperSpacing.Medium))

                GrupperMultilineTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = "Comment",
                    placeholder = "Share your thoughts...",
                    minLines = 3,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            GrupperPrimaryButton(
                text = "Post",
                onClick = {
                    val name = authorName.ifBlank { "Anonymous" }
                    onSubmit(name, content)
                },
                isLoading = isSubmitting,
                enabled = content.isNotBlank() && !isSubmitting
            )
        },
        dismissButton = {
            GrupperTextButton(
                text = "Cancel",
                onClick = onDismiss,
                enabled = !isSubmitting
            )
        }
    )
}

/**
 * Parse color hex string to Color
 * Handles both #RRGGBB and RRGGBB formats
 * Cross-platform compatible (works on Android and iOS)
 */
private fun parseColor(colorHex: String): Long {
    val hex = colorHex.removePrefix("#")
    return ("FF" + hex).toLong(16)
}

/**
 * Count total comments including nested replies
 */
private fun countAllComments(comments: List<Comment>): Int {
    return comments.sumOf { 1 + countAllComments(it.replies) }
}
