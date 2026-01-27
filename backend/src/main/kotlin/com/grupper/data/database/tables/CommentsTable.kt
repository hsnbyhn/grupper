package com.grupper.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object CommentsTable : Table("comments") {
    val id = long("id").autoIncrement()
    val postId = long("post_id").references(PostsTable.id, onDelete = ReferenceOption.CASCADE)
    val parentId = long("parent_id").references(id, onDelete = ReferenceOption.CASCADE).nullable()
    val authorName = varchar("author_name", 50)
    val content = text("content")
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)
}
