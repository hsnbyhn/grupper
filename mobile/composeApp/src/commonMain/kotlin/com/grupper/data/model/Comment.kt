package com.grupper.data.model

import kotlinx.serialization.Serializable

/**
 * Comment data model
 * Matches backend API contract
 * Supports nested/threaded comments via replies list
 */
@Serializable
data class Comment(
    val id: Long,
    val postId: Long,
    val parentId: Long? = null,
    val authorName: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val replies: List<Comment> = emptyList()
)

@Serializable
data class CreateCommentRequest(
    val authorName: String,
    val content: String
)

@Serializable
data class UpdateCommentRequest(
    val content: String
)
