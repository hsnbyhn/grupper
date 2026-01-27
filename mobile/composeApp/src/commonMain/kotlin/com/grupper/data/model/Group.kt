package com.grupper.data.model

import kotlinx.serialization.Serializable

/**
 * Group data model
 * Matches backend API contract
 */
@Serializable
data class Group(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String? = null,
    val memberCount: Int = 0,
    val postCount: Int = 0,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateGroupRequest(
    val name: String,
    val description: String,
    val imageUrl: String? = null
)

@Serializable
data class UpdateGroupRequest(
    val name: String? = null,
    val description: String? = null,
    val imageUrl: String? = null
)
