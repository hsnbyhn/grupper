package com.grupper.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: Long,
    val groupId: Long,
    val title: String,
    val content: String,
    val authorName: String,
    val imageUrl: String? = null,
    val tag: Tag? = null,
    val commentCount: Int = 0,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreatePostRequest(
    val title: String,
    val content: String,
    val authorName: String,
    val tagId: Long,
    val imageUrl: String? = null
) {
    fun validate() {
        require(title.isNotBlank()) { "Post title is required" }
        require(title.length <= 100) { "Post title must be 100 characters or less" }
        require(content.isNotBlank()) { "Post content is required" }
        require(content.length <= 5000) { "Post content must be 5000 characters or less" }
        require(authorName.isNotBlank()) { "Author name is required" }
        require(authorName.length <= 50) { "Author name must be 50 characters or less" }
    }
}

@Serializable
data class UpdatePostRequest(
    val title: String? = null,
    val content: String? = null,
    val tagId: Long? = null,
    val imageUrl: String? = null
) {
    fun validate() {
        title?.let {
            require(it.isNotBlank()) { "Post title cannot be blank" }
            require(it.length <= 100) { "Post title must be 100 characters or less" }
        }
        content?.let {
            require(it.isNotBlank()) { "Post content cannot be blank" }
            require(it.length <= 5000) { "Post content must be 5000 characters or less" }
        }
    }
}
