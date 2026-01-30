package com.grupper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupper.data.api.CommentService
import com.grupper.data.api.PostService
import com.grupper.data.model.Comment
import com.grupper.data.model.Post
import kotlinx.coroutines.launch

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
                val post = PostService.getPostById(postId)
                val comments = CommentService.getCommentsByPostId(postId)

                uiState = uiState.copy(
                    isLoading = false,
                    post = post,
                    comments = comments
                )
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
                    val comments = CommentService.getCommentsByPostId(uiState.post!!.id)

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
                val newComment = CommentService.createComment(
                    postId = uiState.post!!.id,
                    authorName = authorName,
                    content = content
                )

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
                val newReply = CommentService.createReply(
                    parentCommentId = parentComment.id,
                    authorName = authorName,
                    content = content
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
