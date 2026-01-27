package com.grupper.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val success: Boolean = false,
    val data: String? = null,
    val error: ApiError
)

@Serializable
data class ApiError(
    val code: String,
    val message: String
)

fun Application.configureStatusPages() {
    install(StatusPages) {
        // Handle 404 Not Found
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status,
                ErrorResponse(
                    error = ApiError(
                        code = "NOT_FOUND",
                        message = "The requested resource was not found"
                    )
                )
            )
        }

        // Handle generic exceptions
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    error = ApiError(
                        code = "INTERNAL_ERROR",
                        message = cause.message ?: "An unexpected error occurred"
                    )
                )
            )
        }

        // Handle IllegalArgumentException (validation errors)
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    error = ApiError(
                        code = "VALIDATION_ERROR",
                        message = cause.message ?: "Invalid request data"
                    )
                )
            )
        }
    }
}
