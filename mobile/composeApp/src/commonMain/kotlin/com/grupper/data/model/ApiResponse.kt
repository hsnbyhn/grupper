package com.grupper.data.model

import kotlinx.serialization.Serializable

/**
 * Standard API response wrapper
 * Matches backend API contract
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null,
    val pagination: PaginationInfo? = null
)

@Serializable
data class ApiError(
    val code: String,
    val message: String
)

@Serializable
data class PaginationInfo(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int,
    val hasMore: Boolean
)

/**
 * Exception thrown when API returns an error
 */
class ApiException(
    val error: ApiError
) : Exception(error.message) {
    val code: String get() = error.code
}

/**
 * Common API error codes
 */
object ApiErrorCodes {
    const val NOT_FOUND = "NOT_FOUND"
    const val VALIDATION_ERROR = "VALIDATION_ERROR"
    const val INTERNAL_ERROR = "INTERNAL_ERROR"
    const val NETWORK_ERROR = "NETWORK_ERROR"
}
