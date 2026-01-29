package com.grupper.routes

import com.grupper.data.repository.GroupRepositoryImpl
import com.grupper.domain.model.CreateGroupRequest
import com.grupper.domain.model.UpdateGroupRequest
import com.grupper.domain.repository.GroupRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class GroupListResponse(
    val groups: List<com.grupper.domain.model.Group>,
    val totalCount: Long,
    val page: Int,
    val limit: Int
)

fun Route.groupRoutes() {
    val repository: GroupRepository = GroupRepositoryImpl()

    route("/groups") {
        // GET /api/v1/groups?page=1&limit=20
        get {
            try {
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                val groups = repository.getAll(page, limit)
                val totalCount = repository.getTotalCount()

                call.respond(
                    HttpStatusCode.OK,
                    GroupListResponse(groups, totalCount, page, limit)
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // GET /api/v1/groups/{id}
        get("/{id}") {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid group ID"))

                val group = repository.getById(id)
                if (group != null) {
                    call.respond(HttpStatusCode.OK, group)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Group not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // POST /api/v1/groups
        post {
            try {
                val request = call.receive<CreateGroupRequest>()
                request.validate()

                val group = repository.create(request)
                call.respond(HttpStatusCode.Created, group)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // PUT /api/v1/groups/{id}
        put("/{id}") {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid group ID"))

                val request = call.receive<UpdateGroupRequest>()
                request.validate()

                val group = repository.update(id, request)
                if (group != null) {
                    call.respond(HttpStatusCode.OK, group)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Group not found"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // DELETE /api/v1/groups/{id}
        delete("/{id}") {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid group ID"))

                val deleted = repository.delete(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Group deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Group not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}
