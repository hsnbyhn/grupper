package com.grupper.domain.repository

import com.grupper.domain.model.Comment
import com.grupper.domain.model.CreateCommentRequest
import com.grupper.domain.model.UpdateCommentRequest

interface CommentRepository {
    suspend fun create(postId: Long, request: CreateCommentRequest): Comment
    suspend fun createReply(commentId: Long, request: CreateCommentRequest): Comment
    suspend fun getById(id: Long): Comment?
    suspend fun getByPostId(postId: Long): List<Comment>
    suspend fun update(id: Long, request: UpdateCommentRequest): Comment?
    suspend fun delete(id: Long): Boolean
}
