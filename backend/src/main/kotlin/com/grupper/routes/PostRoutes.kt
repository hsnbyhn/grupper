package com.grupper.routes

import com.grupper.data.repository.GroupRepositoryImpl
import com.grupper.data.repository.PostRepositoryImpl
import com.grupper.domain.model.CreatePostRequest
import com.grupper.domain.model.UpdatePostRequest
import com.grupper.domain.repository.GroupRepository
import com.grupper.domain.repository.PostRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class PostListResponse(
    val posts: List<com.grupper.domain.model.Post>,
    val totalCount: Long,
    val page: Int,
    val limit: Int
)

fun Route.postRoutes() {
    val repository: PostRepository = PostRepositoryImpl()
    val groupRepository: GroupRepository = GroupRepositoryImpl()

    route("/groups/{groupId}/posts") {
        // GET /api/v1/groups/{groupId}/posts?tag=1&sort=newest&page=1&limit=20
        get {
            try {
                val groupId = call.parameters["groupId"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid group ID"))

                val tagId = call.request.queryParameters["tag"]?.toLongOrNull()
                val sort = call.request.queryParameters["sort"] ?: "newest"
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                // Validate sort parameter
                if (sort !in listOf("newest", "oldest", "most_commented")) {
                    return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Invalid sort parameter. Must be: newest, oldest, or most_commented")
                    )
                }

                val posts = repository.getByGroupId(groupId, tagId, sort, page, limit)
                val totalCount = repository.getTotalCount(groupId, tagId)

                call.respond(
                    HttpStatusCode.OK,
                    PostListResponse(posts, totalCount, page, limit)
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // POST /api/v1/groups/{groupId}/posts
        post {
            try {
                val groupId = call.parameters["groupId"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid group ID"))

                val request = call.receive<CreatePostRequest>()
                request.validate()

                val post = repository.create(groupId, request)
                
                // Increment group's post count
                groupRepository.incrementPostCount(groupId)

                call.respond(HttpStatusCode.Created, post)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }

    route("/posts/{id}") {
        // GET /api/v1/posts/{id}
        get {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid post ID"))

                val post = repository.getById(id)
                if (post != null) {
                    call.respond(HttpStatusCode.OK, post)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // PUT /api/v1/posts/{id}
        put {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid post ID"))

                val request = call.receive<UpdatePostRequest>()
                request.validate()

                val post = repository.update(id, request)
                if (post != null) {
                    call.respond(HttpStatusCode.OK, post)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // DELETE /api/v1/posts/{id}
        delete {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid post ID"))

                // Get post first to get groupId for decrementing count
                val post = repository.getById(id)
                if (post == null) {
                    return@delete call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found"))
                }

                val deleted = repository.delete(id)
                if (deleted) {
                    // Decrement group's post count
                    groupRepository.decrementPostCount(post.groupId)
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Post deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Post not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}
