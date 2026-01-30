package com.grupper.data.api

import com.grupper.data.model.CreateTagRequest
import com.grupper.data.model.Tag
import com.grupper.data.model.UpdateTagRequest

/**
 * API service for Tag operations
 */
object TagService {

    /**
     * Get all tags for a group
     */
    suspend fun getTagsByGroupId(groupId: Long): List<Tag> {
        return ApiClient.get("/groups/$groupId/tags")
    }

    /**
     * Create new tag
     */
    suspend fun createTag(groupId: Long, name: String, color: String): Tag {
        val request = CreateTagRequest(
            name = name,
            color = color
        )
        return ApiClient.post("/groups/$groupId/tags", request)
    }

    /**
     * Update existing tag
     */
    suspend fun updateTag(tagId: Long, name: String, color: String): Tag {
        val request = UpdateTagRequest(
            name = name,
            color = color
        )
        return ApiClient.put("/tags/$tagId", request)
    }

    /**
     * Delete tag
     */
    suspend fun deleteTag(tagId: Long) {
        ApiClient.delete<Unit>("/tags/$tagId")
    }
}
