package com.grupper.plugins

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

        // API v1 routes will be added here
        route("/api/v1") {
            get {
                call.respond(
                    ApiInfoResponse(
                        message = "Welcome to Grupper API v1",
                        endpoints = listOf(
                            "/api/v1/groups",
                            "/api/v1/posts",
                            "/api/v1/comments",
                            "/api/v1/upload"
                        )
                    )
                )
            }
        }
    }
}
