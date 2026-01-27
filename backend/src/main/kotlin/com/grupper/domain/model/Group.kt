package com.grupper.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: Long,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val memberCount: Int = 0,
    val postCount: Int = 0,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateGroupRequest(
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null
) {
    fun validate() {
        require(name.isNotBlank()) { "Group name is required" }
        require(name.length <= 50) { "Group name must be 50 characters or less" }
        description?.let {
            require(it.length <= 500) { "Description must be 500 characters or less" }
        }
    }
}

@Serializable
data class UpdateGroupRequest(
    val name: String? = null,
    val description: String? = null,
    val imageUrl: String? = null
) {
    fun validate() {
        name?.let {
            require(it.isNotBlank()) { "Group name cannot be blank" }
            require(it.length <= 50) { "Group name must be 50 characters or less" }
        }
        description?.let {
            require(it.length <= 500) { "Description must be 500 characters or less" }
        }
    }
}
