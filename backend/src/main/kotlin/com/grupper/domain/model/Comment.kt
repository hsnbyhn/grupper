package com.grupper.domain.model

import kotlinx.serialization.Serializable

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
) {
    fun validate() {
        require(authorName.isNotBlank()) { "Author name is required" }
        require(authorName.length <= 50) { "Author name must be 50 characters or less" }
        require(content.isNotBlank()) { "Comment content is required" }
        require(content.length <= 2000) { "Comment must be 2000 characters or less" }
    }
}

@Serializable
data class UpdateCommentRequest(
    val content: String
) {
    fun validate() {
        require(content.isNotBlank()) { "Comment content is required" }
        require(content.length <= 2000) { "Comment must be 2000 characters or less" }
    }
}
