package com.grupper.data.repository

import com.grupper.data.database.tables.GroupsTable
import com.grupper.domain.model.CreateGroupRequest
import com.grupper.domain.model.Group
import com.grupper.domain.model.UpdateGroupRequest
import com.grupper.domain.repository.GroupRepository
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class GroupRepositoryImpl : GroupRepository {

    private fun ResultRow.toGroup(): Group = Group(
        id = this[GroupsTable.id],
        name = this[GroupsTable.name],
        description = this[GroupsTable.description],
        imageUrl = this[GroupsTable.imageUrl],
        memberCount = this[GroupsTable.memberCount],
        postCount = this[GroupsTable.postCount],
        createdAt = this[GroupsTable.createdAt].toString(),
        updatedAt = this[GroupsTable.updatedAt].toString()
    )

    override suspend fun create(request: CreateGroupRequest): Group = newSuspendedTransaction {
        val now = Clock.System.now()
        val id = GroupsTable.insert {
            it[name] = request.name
            it[description] = request.description
            it[imageUrl] = request.imageUrl
            it[memberCount] = 0
            it[postCount] = 0
            it[createdAt] = now
            it[updatedAt] = now
        } get GroupsTable.id

        GroupsTable.selectAll().where { GroupsTable.id eq id }.single().toGroup()
    }

    override suspend fun getById(id: Long): Group? = newSuspendedTransaction {
        GroupsTable.selectAll().where { GroupsTable.id eq id }.singleOrNull()?.toGroup()
    }

    override suspend fun getAll(page: Int, limit: Int): List<Group> = newSuspendedTransaction {
        val offset = ((page - 1) * limit).toLong()
        GroupsTable
            .selectAll()
            .orderBy(GroupsTable.createdAt, SortOrder.DESC)
            .limit(limit)
            .offset(offset)
            .map { it.toGroup() }
    }

    override suspend fun getTotalCount(): Long = newSuspendedTransaction {
        GroupsTable.selectAll().count()
    }

    override suspend fun update(id: Long, request: UpdateGroupRequest): Group? = newSuspendedTransaction {
        val updated = GroupsTable.update({ GroupsTable.id eq id }) {
            request.name?.let { name -> it[GroupsTable.name] = name }
            request.description?.let { desc -> it[description] = desc }
            request.imageUrl?.let { url -> it[imageUrl] = url }
            it[updatedAt] = Clock.System.now()
        }

        if (updated > 0) {
            GroupsTable.selectAll().where { GroupsTable.id eq id }.singleOrNull()?.toGroup()
        } else {
            null
        }
    }

    override suspend fun delete(id: Long): Boolean = newSuspendedTransaction {
        GroupsTable.deleteWhere { GroupsTable.id eq id } > 0
    }

    override suspend fun incrementPostCount(id: Long): Unit = newSuspendedTransaction {
        val current = GroupsTable.selectAll().where { GroupsTable.id eq id }
            .singleOrNull()?.get(GroupsTable.postCount) ?: 0
        GroupsTable.update({ GroupsTable.id eq id }) {
            it[postCount] = current + 1
        }
    }

    override suspend fun decrementPostCount(id: Long): Unit = newSuspendedTransaction {
        val current = GroupsTable.selectAll().where { GroupsTable.id eq id }
            .singleOrNull()?.get(GroupsTable.postCount) ?: 0
        GroupsTable.update({ GroupsTable.id eq id }) {
            it[postCount] = maxOf(0, current - 1)
        }
    }
}
