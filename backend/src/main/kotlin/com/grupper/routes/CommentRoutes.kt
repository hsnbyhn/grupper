package com.grupper.routes

import com.grupper.data.repository.CommentRepositoryImpl
import com.grupper.data.repository.PostRepositoryImpl
import com.grupper.domain.model.CreateCommentRequest
import com.grupper.domain.model.UpdateCommentRequest
import com.grupper.domain.repository.CommentRepository
import com.grupper.domain.repository.PostRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.commentRoutes() {
    val repository: CommentRepository = CommentRepositoryImpl()
    val postRepository: PostRepository = PostRepositoryImpl()

    route("/posts/{postId}/comments") {
        // GET /api/v1/posts/{postId}/comments
        get {
            try {
                val postId = call.parameters["postId"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid post ID"))

                val comments = repository.getByPostId(postId)
                call.respond(HttpStatusCode.OK, comments)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // POST /api/v1/posts/{postId}/comments
        post {
            try {
                val postId = call.parameters["postId"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid post ID"))

                val request = call.receive<CreateCommentRequest>()
                request.validate()

                val comment = repository.create(postId, request)
                
                // Increment post's comment count
                postRepository.incrementCommentCount(postId)

                call.respond(HttpStatusCode.Created, comment)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }

    route("/comments/{id}") {
        // GET /api/v1/comments/{id}
        get {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid comment ID"))

                val comment = repository.getById(id)
                if (comment != null) {
                    call.respond(HttpStatusCode.OK, comment)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Comment not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // PUT /api/v1/comments/{id}
        put {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid comment ID"))

                val request = call.receive<UpdateCommentRequest>()
                request.validate()

                val comment = repository.update(id, request)
                if (comment != null) {
                    call.respond(HttpStatusCode.OK, comment)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Comment not found"))
                }
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // DELETE /api/v1/comments/{id}
        delete {
            try {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid comment ID"))

                // Get comment first to get postId for decrementing count
                val comment = repository.getById(id)
                if (comment == null) {
                    return@delete call.respond(HttpStatusCode.NotFound, mapOf("error" to "Comment not found"))
                }

                val deleted = repository.delete(id)
                if (deleted) {
                    // Decrement post's comment count (only if it's a top-level comment)
                    if (comment.parentId == null) {
                        postRepository.decrementCommentCount(comment.postId)
                    }
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Comment deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Comment not found"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // POST /api/v1/comments/{id}/reply
        post("/reply") {
            try {
                val commentId = call.parameters["id"]?.toLongOrNull()
                    ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid comment ID"))

                val request = call.receive<CreateCommentRequest>()
                request.validate()

                val reply = repository.createReply(commentId, request)
                call.respond(HttpStatusCode.Created, reply)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}
