package com.grupper.data.repository

import com.grupper.data.database.tables.PostsTable
import com.grupper.data.database.tables.TagsTable
import com.grupper.domain.model.CreatePostRequest
import com.grupper.domain.model.Post
import com.grupper.domain.model.Tag
import com.grupper.domain.model.UpdatePostRequest
import com.grupper.domain.repository.PostRepository
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class PostRepositoryImpl : PostRepository {

    private fun ResultRow.toPost(): Post {
        val tagId = this.getOrNull(PostsTable.tagId)
        val tag = if (tagId != null && hasValue(TagsTable.id)) {
            Tag(
                id = this[TagsTable.id],
                groupId = this[TagsTable.groupId],
                name = this[TagsTable.name],
                color = this[TagsTable.color],
                createdAt = this[TagsTable.createdAt].toString()
            )
        } else null

        return Post(
            id = this[PostsTable.id],
            groupId = this[PostsTable.groupId],
            title = this[PostsTable.title],
            content = this[PostsTable.content],
            authorName = this[PostsTable.authorName],
            imageUrl = this[PostsTable.imageUrl],
            tag = tag,
            commentCount = this[PostsTable.commentCount],
            createdAt = this[PostsTable.createdAt].toString(),
            updatedAt = this[PostsTable.updatedAt].toString()
        )
    }

    override suspend fun create(groupId: Long, request: CreatePostRequest): Post = newSuspendedTransaction {
        val now = Clock.System.now()
        val id = PostsTable.insert {
            it[PostsTable.groupId] = groupId
            it[tagId] = request.tagId
            it[title] = request.title
            it[content] = request.content
            it[authorName] = request.authorName
            it[imageUrl] = request.imageUrl
            it[commentCount] = 0
            it[createdAt] = now
            it[updatedAt] = now
        } get PostsTable.id

        // Join with TagsTable to get tag info
        (PostsTable leftJoin TagsTable)
            .selectAll()
            .where { PostsTable.id eq id }
            .single()
            .toPost()
    }

    override suspend fun getById(id: Long): Post? = newSuspendedTransaction {
        (PostsTable leftJoin TagsTable)
            .selectAll()
            .where { PostsTable.id eq id }
            .singleOrNull()
            ?.toPost()
    }

    override suspend fun getByGroupId(
        groupId: Long,
        tagId: Long?,
        sort: String,
        page: Int,
        limit: Int
    ): List<Post> = newSuspendedTransaction {
        val offset = ((page - 1) * limit).toLong()

        var query = (PostsTable leftJoin TagsTable)
            .selectAll()
            .where { PostsTable.groupId eq groupId }

        // Filter by tag if specified
        tagId?.let {
            query = query.andWhere { PostsTable.tagId eq it }
        }

        // Apply sorting
        query = when (sort) {
            "oldest" -> query.orderBy(PostsTable.createdAt, SortOrder.ASC)
            "most_commented" -> query.orderBy(PostsTable.commentCount, SortOrder.DESC)
            else -> query.orderBy(PostsTable.createdAt, SortOrder.DESC) // newest (default)
        }

        query
            .limit(limit)
            .offset(offset)
            .map { it.toPost() }
    }

    override suspend fun getTotalCount(groupId: Long, tagId: Long?): Long = newSuspendedTransaction {
        var query = PostsTable.selectAll().where { PostsTable.groupId eq groupId }
        
        tagId?.let {
            query = query.andWhere { PostsTable.tagId eq it }
        }
        
        query.count()
    }

    override suspend fun update(id: Long, request: UpdatePostRequest): Post? = newSuspendedTransaction {
        val updated = PostsTable.update({ PostsTable.id eq id }) {
            request.title?.let { title -> it[PostsTable.title] = title }
            request.content?.let { content -> it[PostsTable.content] = content }
            request.tagId?.let { tag -> it[tagId] = tag }
            request.imageUrl?.let { url -> it[imageUrl] = url }
            it[updatedAt] = Clock.System.now()
        }

        if (updated > 0) {
            (PostsTable leftJoin TagsTable)
                .selectAll()
                .where { PostsTable.id eq id }
                .singleOrNull()
                ?.toPost()
        } else {
            null
        }
    }

    override suspend fun delete(id: Long): Boolean = newSuspendedTransaction {
        PostsTable.deleteWhere { PostsTable.id eq id } > 0
    }

    override suspend fun incrementCommentCount(id: Long): Unit = newSuspendedTransaction {
        val current = PostsTable.selectAll().where { PostsTable.id eq id }
            .singleOrNull()?.get(PostsTable.commentCount) ?: 0
        PostsTable.update({ PostsTable.id eq id }) {
            it[commentCount] = current + 1
        }
    }

    override suspend fun decrementCommentCount(id: Long): Unit = newSuspendedTransaction {
        val current = PostsTable.selectAll().where { PostsTable.id eq id }
            .singleOrNull()?.get(PostsTable.commentCount) ?: 0
        PostsTable.update({ PostsTable.id eq id }) {
            it[commentCount] = maxOf(0, current - 1)
        }
    }
}
