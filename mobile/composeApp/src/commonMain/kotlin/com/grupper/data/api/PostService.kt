package com.grupper.data.api

import com.grupper.data.model.CreatePostRequest
import com.grupper.data.model.Post
import com.grupper.data.model.PostSortOrder
import com.grupper.data.model.PostsResponse
import com.grupper.data.model.UpdatePostRequest
import io.ktor.client.request.*

/**
 * API service for Post operations
 */
object PostService {

    /**
     * Get all posts for a group with optional filtering and sorting
     */
    suspend fun getPostsByGroupId(
        groupId: Long,
        tagId: Long? = null,
        sortOrder: PostSortOrder = PostSortOrder.NEWEST
    ): List<Post> {
        val response: PostsResponse = ApiClient.get("/groups/$groupId/posts") {
            if (tagId != null) {
                parameter("tag", tagId)
            }
            parameter("sort", sortOrder.value)
        }
        return response.posts
    }

    /**
     * Get post by ID
     */
    suspend fun getPostById(id: Long): Post {
        return ApiClient.get("/posts/$id")
    }

    /**
     * Create new post
     */
    suspend fun createPost(
        groupId: Long,
        tagId: Long,
        title: String,
        content: String,
        authorName: String,
        imageUrl: String? = null
    ): Post {
        val request = CreatePostRequest(
            title = title,
            content = content,
            authorName = authorName,
            tagId = tagId,
            imageUrl = imageUrl
        )
        return ApiClient.post("/groups/$groupId/posts", request)
    }

    /**
     * Update existing post
     */
    suspend fun updatePost(
        id: Long,
        title: String,
        content: String,
        imageUrl: String? = null
    ): Post {
        val request = UpdatePostRequest(
            title = title,
            content = content,
            imageUrl = imageUrl
        )
        return ApiClient.put("/posts/$id", request)
    }

    /**
     * Delete post
     */
    suspend fun deletePost(id: Long) {
        ApiClient.delete<Unit>("/posts/$id")
    }
}
