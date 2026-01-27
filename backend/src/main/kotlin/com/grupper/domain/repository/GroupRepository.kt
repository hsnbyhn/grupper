package com.grupper.domain.repository

import com.grupper.domain.model.CreateGroupRequest
import com.grupper.domain.model.Group
import com.grupper.domain.model.UpdateGroupRequest

interface GroupRepository {
    suspend fun create(request: CreateGroupRequest): Group
    suspend fun getById(id: Long): Group?
    suspend fun getAll(page: Int, limit: Int): List<Group>
    suspend fun getTotalCount(): Long
    suspend fun update(id: Long, request: UpdateGroupRequest): Group?
    suspend fun delete(id: Long): Boolean
    suspend fun incrementPostCount(id: Long)
    suspend fun decrementPostCount(id: Long)
}
