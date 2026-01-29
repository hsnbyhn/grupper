package com.grupper.data.repository

import com.grupper.data.database.tables.TagsTable
import com.grupper.domain.model.CreateTagRequest
import com.grupper.domain.model.Tag
import com.grupper.domain.model.UpdateTagRequest
import com.grupper.domain.repository.TagRepository
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class TagRepositoryImpl : TagRepository {

    private fun ResultRow.toTag(): Tag = Tag(
        id = this[TagsTable.id],
        groupId = this[TagsTable.groupId],
        name = this[TagsTable.name],
        color = this[TagsTable.color],
        createdAt = this[TagsTable.createdAt].toString()
    )

    override suspend fun create(groupId: Long, request: CreateTagRequest): Tag = newSuspendedTransaction {
        val id = TagsTable.insert {
            it[TagsTable.groupId] = groupId
            it[name] = request.name
            it[color] = request.color
            it[createdAt] = Clock.System.now()
        } get TagsTable.id

        TagsTable.selectAll().where { TagsTable.id eq id }.single().toTag()
    }

    override suspend fun getById(id: Long): Tag? = newSuspendedTransaction {
        TagsTable.selectAll().where { TagsTable.id eq id }.singleOrNull()?.toTag()
    }

    override suspend fun getByGroupId(groupId: Long): List<Tag> = newSuspendedTransaction {
        TagsTable
            .selectAll()
            .where { TagsTable.groupId eq groupId }
            .orderBy(TagsTable.createdAt, SortOrder.ASC)
            .map { it.toTag() }
    }

    override suspend fun update(id: Long, request: UpdateTagRequest): Tag? = newSuspendedTransaction {
        val updated = TagsTable.update({ TagsTable.id eq id }) {
            request.name?.let { name -> it[TagsTable.name] = name }
            request.color?.let { color -> it[TagsTable.color] = color }
        }

        if (updated > 0) {
            TagsTable.selectAll().where { TagsTable.id eq id }.singleOrNull()?.toTag()
        } else {
            null
        }
    }

    override suspend fun delete(id: Long): Boolean = newSuspendedTransaction {
        TagsTable.deleteWhere { TagsTable.id eq id } > 0
    }
}
