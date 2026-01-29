package com.grupper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupper.data.model.Comment
import com.grupper.data.model.Post
import com.grupper.data.repository.MockData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel for Post Detail screen
 * Manages post data and nested comments with reply functionality
 */
class PostDetailViewModel : ViewModel() {

    var uiState by mutableStateOf(PostDetailUiState())
        private set

    /**
     * Load post and comments by post ID
     */
    fun loadPost(postId: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            try {
                // Simulate network delay
                delay(500)

                // Get post from mock data
                val post = MockData.posts.find { it.id == postId }

                if (post != null) {
                    // Load comments for this post
                    val comments = MockData.getCommentsForPost(postId)

                    uiState = uiState.copy(
                        isLoading = false,
                        post = post,
                        comments = comments
                    )
                } else {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = "Post not found"
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    error = "Failed to load post: ${e.message}"
                )
            }
        }
    }

    /**
     * Refresh post and comments
     */
    fun refreshPost() {
        if (uiState.post != null) {
            viewModelScope.launch {
                uiState = uiState.copy(isRefreshing = true)

                try {
                    delay(800)

                    // Reload comments
                    val comments = MockData.getCommentsForPost(uiState.post!!.id)

                    uiState = uiState.copy(
                        isRefreshing = false,
                        comments = comments
                    )
                } catch (e: Exception) {
                    uiState = uiState.copy(isRefreshing = false)
                }
            }
        }
    }

    /**
     * Add a new top-level comment
     */
    fun addComment(authorName: String, content: String) {
        if (uiState.post == null || content.isBlank()) return

        viewModelScope.launch {
            uiState = uiState.copy(isSubmittingComment = true)

            try {
                delay(500)

                // Create new comment
                val newComment = Comment(
                    id = Random.nextLong(100000, 999999), // Mock ID
                    postId = uiState.post!!.id,
                    parentId = null,
                    authorName = authorName,
                    content = content,
                    createdAt = "2025-01-29T10:00:00Z", // Mock timestamp
                    updatedAt = "2025-01-29T10:00:00Z",
                    replies = emptyList()
                )

                // Add to comments list
                uiState = uiState.copy(
                    isSubmittingComment = false,
                    comments = uiState.comments + newComment
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSubmittingComment = false,
                    error = "Failed to post comment"
                )
            }
        }
    }

    /**
     * Add a reply to an existing comment
     */
    fun addReply(parentComment: Comment, authorName: String, content: String) {
        if (uiState.post == null || content.isBlank()) return

        viewModelScope.launch {
            uiState = uiState.copy(isSubmittingComment = true)

            try {
                delay(500)

                // Create new reply
                val newReply = Comment(
                    id = Random.nextLong(100000, 999999), // Mock ID
                    postId = uiState.post!!.id,
                    parentId = parentComment.id,
                    authorName = authorName,
                    content = content,
                    createdAt = "2025-01-29T10:00:00Z",
                    updatedAt = "2025-01-29T10:00:00Z",
                    replies = emptyList()
                )

                // Add reply to parent comment (recursive update)
                val updatedComments = addReplyToComment(uiState.comments, parentComment.id, newReply)

                uiState = uiState.copy(
                    isSubmittingComment = false,
                    comments = updatedComments
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSubmittingComment = false,
                    error = "Failed to post reply"
                )
            }
        }
    }

    /**
     * Recursively add reply to nested comment structure
     */
    private fun addReplyToComment(
        comments: List<Comment>,
        parentId: Long,
        reply: Comment
    ): List<Comment> {
        return comments.map { comment ->
            if (comment.id == parentId) {
                comment.copy(replies = comment.replies + reply)
            } else if (comment.replies.isNotEmpty()) {
                comment.copy(replies = addReplyToComment(comment.replies, parentId, reply))
            } else {
                comment
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        uiState = uiState.copy(error = null)
    }
}

/**
 * UI state for Post Detail screen
 */
data class PostDetailUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSubmittingComment: Boolean = false,
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val error: String? = null
) {
    val hasError: Boolean get() = error != null && !isLoading
    val hasNoComments: Boolean get() = !isLoading && comments.isEmpty() && post != null
}
