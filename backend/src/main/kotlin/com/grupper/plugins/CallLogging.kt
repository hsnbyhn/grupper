package com.grupper.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

fun Application.configureCallLogging() {
    install(CallLogging) {
        level = Level.INFO

        // Log request path and method
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val path = call.request.path()
            val duration = call.processingTimeMillis()

            "[$httpMethod] $path -> $status (${duration}ms)"
        }

        // Filter out health check requests from logs (optional, reduces noise)
        filter { call ->
            !call.request.path().startsWith("/health")
        }
    }
}
