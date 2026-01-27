package com.grupper.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Tag(
    val id: Long,
    val groupId: Long,
    val name: String,
    val color: String,
    val createdAt: String
)

@Serializable
data class CreateTagRequest(
    val name: String,
    val color: String
) {
    fun validate() {
        require(name.isNotBlank()) { "Tag name is required" }
        require(name.length <= 30) { "Tag name must be 30 characters or less" }
        require(color.matches(Regex("^#[0-9A-Fa-f]{6}$"))) { "Color must be a valid hex color (e.g., #8B5CF6)" }
    }
}

@Serializable
data class UpdateTagRequest(
    val name: String? = null,
    val color: String? = null
) {
    fun validate() {
        name?.let {
            require(it.isNotBlank()) { "Tag name cannot be blank" }
            require(it.length <= 30) { "Tag name must be 30 characters or less" }
        }
        color?.let {
            require(it.matches(Regex("^#[0-9A-Fa-f]{6}$"))) { "Color must be a valid hex color (e.g., #8B5CF6)" }
        }
    }
}
