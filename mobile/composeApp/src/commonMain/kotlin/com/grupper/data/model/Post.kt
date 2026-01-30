package com.grupper.data.model

import kotlinx.serialization.Serializable

/**
 * Post data model
 * Matches backend API contract
 */
@Serializable
data class Post(
    val id: Long,
    val groupId: Long,
    val title: String,
    val content: String,
    val authorName: String,
    val imageUrl: String? = null,
    val tag: TagInfo? = null,
    val commentCount: Int = 0,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreatePostRequest(
    val title: String,
    val content: String,
    val authorName: String,
    val tagId: Long? = null,
    val imageUrl: String? = null
)

@Serializable
data class UpdatePostRequest(
    val title: String? = null,
    val content: String? = null,
    val tagId: Long? = null,
    val imageUrl: String? = null
)

/**
 * Sorting options for posts
 */
enum class PostSortOrder(val value: String) {
    NEWEST("newest"),
    OLDEST("oldest"),
    MOST_COMMENTED("most_commented")
}

@Serializable
data class PostsResponse(
    val posts: List<Post>,
    val totalCount: Int,
    val page: Int,
    val limit: Int
)
