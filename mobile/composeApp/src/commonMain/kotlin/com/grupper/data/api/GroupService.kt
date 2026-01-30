package com.grupper.data.api

import com.grupper.data.model.CreateGroupRequest
import com.grupper.data.model.Group
import com.grupper.data.model.GroupsResponse
import com.grupper.data.model.UpdateGroupRequest

/**
 * API service for Group operations
 * Calls backend REST API endpoints
 */
object GroupService {

    /**
     * Get all groups
     */
    suspend fun getAllGroups(): List<Group> {
        val response: GroupsResponse = ApiClient.get("/groups")
        return response.groups
    }

    /**
     * Get group by ID
     */
    suspend fun getGroupById(id: Long): Group {
        return ApiClient.get("/groups/$id")
    }

    /**
     * Create new group
     */
    suspend fun createGroup(name: String, description: String, imageUrl: String? = null): Group {
        val request = CreateGroupRequest(
            name = name,
            description = description,
            imageUrl = imageUrl
        )
        return ApiClient.post("/groups", request)
    }

    /**
     * Update existing group
     */
    suspend fun updateGroup(id: Long, name: String, description: String, imageUrl: String? = null): Group {
        val request = UpdateGroupRequest(
            name = name,
            description = description,
            imageUrl = imageUrl
        )
        return ApiClient.put("/groups/$id", request)
    }

    /**
     * Delete group
     */
    suspend fun deleteGroup(id: Long) {
        ApiClient.delete<Unit>("/groups/$id")
    }
}
