package com.grupper.plugins

import com.grupper.routes.commentRoutes
import com.grupper.routes.groupRoutes
import com.grupper.routes.postRoutes
import com.grupper.routes.tagRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class HealthResponse(
    val status: String,
    val version: String,
    val database: String = "unknown"
)

@Serializable
data class ApiInfoResponse(
    val message: String,
    val version: String,
    val endpoints: List<String>
)

fun Application.configureRouting() {
    routing {
        // Health check endpoint for verification
        get("/health") {
            val dbStatus = try {
                transaction {
                    exec("SELECT 1") { it.next() }
                }
                "connected"
            } catch (e: Exception) {
                "disconnected: ${e.message}"
            }

            call.respond(
                HealthResponse(
                    status = "ok",
                    version = "1.0.0",
                    database = dbStatus
                )
            )
        }

        // API v1 routes
        route("/api/v1") {
            get {
                call.respond(
                    ApiInfoResponse(
                        message = "Welcome to Grupper API",
                        version = "1.0.0",
                        endpoints = listOf(
                            "GET    /api/v1/groups",
                            "POST   /api/v1/groups",
                            "GET    /api/v1/groups/{id}",
                            "PUT    /api/v1/groups/{id}",
                            "DELETE /api/v1/groups/{id}",
                            "",
                            "GET    /api/v1/groups/{groupId}/tags",
                            "POST   /api/v1/groups/{groupId}/tags",
                            "PUT    /api/v1/groups/{groupId}/tags/{id}",
                            "DELETE /api/v1/groups/{groupId}/tags/{id}",
                            "",
                            "GET    /api/v1/groups/{groupId}/posts?tag=&sort=&page=&limit=",
                            "POST   /api/v1/groups/{groupId}/posts",
                            "GET    /api/v1/posts/{id}",
                            "PUT    /api/v1/posts/{id}",
                            "DELETE /api/v1/posts/{id}",
                            "",
                            "GET    /api/v1/posts/{postId}/comments",
                            "POST   /api/v1/posts/{postId}/comments",
                            "GET    /api/v1/comments/{id}",
                            "PUT    /api/v1/comments/{id}",
                            "DELETE /api/v1/comments/{id}",
                            "POST   /api/v1/comments/{id}/reply"
                        )
                    )
                )
            }

            // Register all route modules
            groupRoutes()
            tagRoutes()
            postRoutes()
            commentRoutes()
        }
    }
}
