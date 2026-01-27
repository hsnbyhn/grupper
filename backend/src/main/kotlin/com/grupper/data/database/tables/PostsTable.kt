package com.grupper.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object PostsTable : Table("posts") {
    val id = long("id").autoIncrement()
    val groupId = long("group_id").references(GroupsTable.id, onDelete = ReferenceOption.CASCADE)
    val tagId = long("tag_id").references(TagsTable.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val title = varchar("title", 100)
    val content = text("content")
    val authorName = varchar("author_name", 50)
    val imageUrl = varchar("image_url", 500).nullable()
    val commentCount = integer("comment_count").default(0)
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)
}
