package com.grupper.data.repository

import com.grupper.data.database.tables.CommentsTable
import com.grupper.domain.model.Comment
import com.grupper.domain.model.CreateCommentRequest
import com.grupper.domain.model.UpdateCommentRequest
import com.grupper.domain.repository.CommentRepository
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class CommentRepositoryImpl : CommentRepository {

    private fun ResultRow.toComment(): Comment = Comment(
        id = this[CommentsTable.id],
        postId = this[CommentsTable.postId],
        parentId = this[CommentsTable.parentId],
        authorName = this[CommentsTable.authorName],
        content = this[CommentsTable.content],
        createdAt = this[CommentsTable.createdAt].toString(),
        updatedAt = this[CommentsTable.updatedAt].toString(),
        replies = emptyList() // Will be populated by buildNestedComments
    )

    /**
     * Builds a nested comment tree structure from a flat list of comments
     */
    private fun buildNestedComments(comments: List<Comment>): List<Comment> {
        val commentMap = comments.associateBy { it.id }.toMutableMap()
        val rootComments = mutableListOf<Comment>()

        comments.forEach { comment ->
            if (comment.parentId == null) {
                // Top-level comment
                rootComments.add(comment)
            } else {
                // Reply - add to parent's replies list
                val parent = commentMap[comment.parentId]
                parent?.let {
                    val updatedParent = it.copy(replies = it.replies + comment)
                    commentMap[it.id] = updatedParent
                }
            }
        }

        // Update root comments with their nested replies
        return rootComments.map { root ->
            updateCommentWithReplies(root, commentMap)
        }.sortedByDescending { it.createdAt }
    }

    /**
     * Recursively updates a comment with its nested replies
     */
    private fun updateCommentWithReplies(comment: Comment, commentMap: Map<Long, Comment>): Comment {
        val replies = commentMap.values
            .filter { it.parentId == comment.id }
            .map { updateCommentWithReplies(it, commentMap) }
            .sortedBy { it.createdAt }
        
        return comment.copy(replies = replies)
    }

    override suspend fun create(postId: Long, request: CreateCommentRequest): Comment = newSuspendedTransaction {
        val now = Clock.System.now()
        val id = CommentsTable.insert {
            it[CommentsTable.postId] = postId
            it[parentId] = null // Top-level comment
            it[authorName] = request.authorName
            it[content] = request.content
            it[createdAt] = now
            it[updatedAt] = now
        } get CommentsTable.id

        CommentsTable.selectAll().where { CommentsTable.id eq id }.single().toComment()
    }

    override suspend fun createReply(commentId: Long, request: CreateCommentRequest): Comment = newSuspendedTransaction {
        // Get parent comment to extract postId
        val parentComment = CommentsTable.selectAll()
            .where { CommentsTable.id eq commentId }
            .singleOrNull()
            ?: throw IllegalArgumentException("Parent comment not found")

        val now = Clock.System.now()
        val id = CommentsTable.insert {
            it[postId] = parentComment[CommentsTable.postId]
            it[parentId] = commentId
            it[authorName] = request.authorName
            it[content] = request.content
            it[createdAt] = now
            it[updatedAt] = now
        } get CommentsTable.id

        CommentsTable.selectAll().where { CommentsTable.id eq id }.single().toComment()
    }

    override suspend fun getById(id: Long): Comment? = newSuspendedTransaction {
        CommentsTable.selectAll().where { CommentsTable.id eq id }.singleOrNull()?.toComment()
    }

    override suspend fun getByPostId(postId: Long): List<Comment> = newSuspendedTransaction {
        val allComments = CommentsTable
            .selectAll()
            .where { CommentsTable.postId eq postId }
            .orderBy(CommentsTable.createdAt, SortOrder.ASC)
            .map { it.toComment() }

        // Build nested structure
        buildNestedComments(allComments)
    }

    override suspend fun update(id: Long, request: UpdateCommentRequest): Comment? = newSuspendedTransaction {
        val updated = CommentsTable.update({ CommentsTable.id eq id }) {
            it[content] = request.content
            it[updatedAt] = Clock.System.now()
        }

        if (updated > 0) {
            CommentsTable.selectAll().where { CommentsTable.id eq id }.singleOrNull()?.toComment()
        } else {
            null
        }
    }

    override suspend fun delete(id: Long): Boolean = newSuspendedTransaction {
        CommentsTable.deleteWhere { CommentsTable.id eq id } > 0
    }
}
