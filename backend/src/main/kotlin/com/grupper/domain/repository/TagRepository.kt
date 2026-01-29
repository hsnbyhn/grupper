package com.grupper.domain.repository

import com.grupper.domain.model.CreateTagRequest
import com.grupper.domain.model.Tag
import com.grupper.domain.model.UpdateTagRequest

interface TagRepository {
    suspend fun create(groupId: Long, request: CreateTagRequest): Tag
    suspend fun getById(id: Long): Tag?
    suspend fun getByGroupId(groupId: Long): List<Tag>
    suspend fun update(id: Long, request: UpdateTagRequest): Tag?
    suspend fun delete(id: Long): Boolean
}
