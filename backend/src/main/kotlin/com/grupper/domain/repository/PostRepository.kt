package com.grupper.domain.repository

import com.grupper.domain.model.CreatePostRequest
import com.grupper.domain.model.Post
import com.grupper.domain.model.UpdatePostRequest

interface PostRepository {
    suspend fun create(groupId: Long, request: CreatePostRequest): Post
    suspend fun getById(id: Long): Post?
    suspend fun getByGroupId(
        groupId: Long,
        tagId: Long? = null,
        sort: String = "newest",
        page: Int = 1,
        limit: Int = 20
    ): List<Post>
    suspend fun getTotalCount(groupId: Long, tagId: Long? = null): Long
    suspend fun update(id: Long, request: UpdatePostRequest): Post?
    suspend fun delete(id: Long): Boolean
    suspend fun incrementCommentCount(id: Long)
    suspend fun decrementCommentCount(id: Long)
}
