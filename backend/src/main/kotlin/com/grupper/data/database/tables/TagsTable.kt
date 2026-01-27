package com.grupper.data.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object TagsTable : Table("tags") {
    val id = long("id").autoIncrement()
    val groupId = long("group_id").references(GroupsTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 30)
    val color = varchar("color", 7) // Hex color: #RRGGBB
    val createdAt = timestamp("created_at").defaultExpression(org.jetbrains.exposed.sql.kotlin.datetime.CurrentTimestamp)

    override val primaryKey = PrimaryKey(id)
}
