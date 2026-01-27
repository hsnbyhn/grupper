package com.grupper.data.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import kotlinx.datetime.Clock

object GroupsTable : Table("groups") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 50)
    val description = varchar("description", 500).nullable()
    val imageUrl = varchar("image_url", 500).nullable()
    val memberCount = integer("member_count").default(0)
    val postCount = integer("post_count").default(0)
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)
}
