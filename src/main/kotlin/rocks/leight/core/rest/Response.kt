package rocks.leight.core.rest

import io.ktor.application.ApplicationCall
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.response.respond
import rocks.leight.core.rest.discovery.Discovery

data class MessageResponse(val message: String)
data class ErrorResponse(val error: String)
data class LinkResponse(val href: String) {
	constructor(href: Url) : this(href.toString())
}

/**
 * send response with Bad Request status code
 */
suspend fun ApplicationCall.badRequest(error: String) = respond(HttpStatusCode.BadRequest, ErrorResponse(error))

/**
 * send response with Forbidden status code
 */
suspend fun ApplicationCall.forbidden(error: String) = respond(HttpStatusCode.Forbidden, ErrorResponse(error))

/**
 * send response with Unauthorized status code
 */
suspend fun ApplicationCall.unauthorized(error: String) = respond(HttpStatusCode.Unauthorized, ErrorResponse(error))

/**
 * send response with Created status code
 */
suspend fun ApplicationCall.created(href: Url) = respond(HttpStatusCode.Created, LinkResponse(href))

/**
 * send response with No Content status code
 */
suspend fun ApplicationCall.noContent() = respond(HttpStatusCode.NoContent)

/**
 * send response with Not Found status code
 */
suspend fun ApplicationCall.notFound(error: String) = respond(HttpStatusCode.NotFound, ErrorResponse(error))

/**
 * send response with Conflict status code
 */
suspend fun ApplicationCall.conflict(error: String) = respond(HttpStatusCode.Conflict, ErrorResponse(error))

/**
 * send response with Not Implemented status code
 */
suspend fun ApplicationCall.notImplemented(error: String) = respond(HttpStatusCode.NotImplemented, ErrorResponse(error))

/**
 * send response with Internal Server Error status code
 */
suspend fun ApplicationCall.internalServerError(error: String) = respond(HttpStatusCode.InternalServerError, ErrorResponse(error))

/**
 * send response with Accepted status code
 */
suspend fun ApplicationCall.accepted(message: String) = respond(HttpStatusCode.Accepted, MessageResponse(message))

/**
 * send response with Accepted status code
 */
suspend fun ApplicationCall.accepted() = respond(HttpStatusCode.Accepted)

suspend fun ApplicationCall.discovery(discovery: Discovery) = respond(discovery)
