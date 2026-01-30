package com.grupper.data.api

import com.grupper.data.model.Comment
import com.grupper.data.model.CreateCommentRequest
import com.grupper.data.model.UpdateCommentRequest

/**
 * API service for Comment operations
 */
object CommentService {

    /**
     * Get all comments for a post (nested structure)
     */
    suspend fun getCommentsByPostId(postId: Long): List<Comment> {
        return ApiClient.get("/posts/$postId/comments")
    }

    /**
     * Get comment by ID
     */
    suspend fun getCommentById(id: Long): Comment {
        return ApiClient.get("/comments/$id")
    }

    /**
     * Create new top-level comment
     */
    suspend fun createComment(postId: Long, authorName: String, content: String): Comment {
        val request = CreateCommentRequest(
            authorName = authorName,
            content = content
        )
        return ApiClient.post("/posts/$postId/comments", request)
    }

    /**
     * Create reply to existing comment
     */
    suspend fun createReply(parentCommentId: Long, authorName: String, content: String): Comment {
        val request = CreateCommentRequest(
            authorName = authorName,
            content = content
        )
        return ApiClient.post("/comments/$parentCommentId/reply", request)
    }

    /**
     * Update existing comment
     */
    suspend fun updateComment(id: Long, content: String): Comment {
        val request = UpdateCommentRequest(
            content = content
        )
        return ApiClient.put("/comments/$id", request)
    }

    /**
     * Delete comment
     */
    suspend fun deleteComment(id: Long) {
        ApiClient.delete<Unit>("/comments/$id")
    }
}
