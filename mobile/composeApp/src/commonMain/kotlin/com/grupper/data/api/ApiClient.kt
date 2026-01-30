package com.grupper.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Get platform-specific base URL
 * Android: 10.0.2.2 (emulator's special localhost alias)
 * iOS: localhost
 */
expect fun getBaseUrl(): String

/**
 * HTTP client for Grupper API
 * Configured with JSON serialization, logging, and error handling
 */
object ApiClient {

    val BASE_URL: String = getBaseUrl()

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }

    /**
     * GET request
     */
    suspend inline fun <reified T> get(
        path: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): T {
        return httpClient.get("$BASE_URL$path") {
            block()
        }.body()
    }

    /**
     * POST request
     */
    suspend inline fun <reified T> post(
        path: String,
        body: Any? = null,
        block: HttpRequestBuilder.() -> Unit = {}
    ): T {
        return httpClient.post("$BASE_URL$path") {
            if (body != null) {
                setBody(body)
            }
            block()
        }.body()
    }

    /**
     * PUT request
     */
    suspend inline fun <reified T> put(
        path: String,
        body: Any? = null,
        block: HttpRequestBuilder.() -> Unit = {}
    ): T {
        return httpClient.put("$BASE_URL$path") {
            if (body != null) {
                setBody(body)
            }
            block()
        }.body()
    }

    /**
     * DELETE request
     */
    suspend inline fun <reified T> delete(
        path: String,
        block: HttpRequestBuilder.() -> Unit = {}
    ): T {
        return httpClient.delete("$BASE_URL$path") {
            block()
        }.body()
    }
}
