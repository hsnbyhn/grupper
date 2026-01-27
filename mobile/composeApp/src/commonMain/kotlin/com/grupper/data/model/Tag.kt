package com.grupper.data.model

import kotlinx.serialization.Serializable

/**
 * Tag data model
 * Matches backend API contract
 */
@Serializable
data class Tag(
    val id: Long,
    val groupId: Long,
    val name: String,
    val color: String,
    val createdAt: String
)

/**
 * Embedded tag in Post response (without groupId and createdAt)
 */
@Serializable
data class TagInfo(
    val id: Long,
    val name: String,
    val color: String
)

@Serializable
data class CreateTagRequest(
    val name: String,
    val color: String
)

@Serializable
data class UpdateTagRequest(
    val name: String? = null,
    val color: String? = null
)
