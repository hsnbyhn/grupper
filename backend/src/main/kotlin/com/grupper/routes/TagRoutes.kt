package com.grupper.routes

import com.grupper.data.repository.TagRepositoryImpl
import com.grupper.domain.model.CreateTagRequest
import com.grupper.domain.model.UpdateTagRequest
import com.grupper.domain.repository.TagRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.tagRoutes() {
    val repository: TagRepository = TagRepositoryImpl()

    route("/groups/{groupId}/tags") {
        // GET /api/v1/groups/{groupId}/tags
        get {
            try {
                val groupId = call.parameters["groupId"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid group ID"))

                val tags = repository.getByGroupId(groupId)
                call.respond(HttpStatusCode.OK, tags)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // POST /api/v1/groups/{groupId}/tags
        post {
            try {
                val groupId = call.parameters["groupId"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid group ID"))

                val request = call.receive<CreateTagRequest>()
                request.validate()

                val tag = repository.create(groupId, request)
                call.respond(HttpStatusCode.Created, tag)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // PUT /api/v1/groups/{groupId}/tags/{id}
        put("/{id}") {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid tag ID"))

                val request = call.receive<UpdateTagRequest>()
                request.validate()

                val tag = repository.update(id, request)
                if (tag != null) {
                    call.respond(HttpStatusCode.OK, tag)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Tag not found"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // DELETE /api/v1/groups/{groupId}/tags/{id}
        delete("/{id}") {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid tag ID"))

                val deleted = repository.delete(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Tag deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Tag not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}
