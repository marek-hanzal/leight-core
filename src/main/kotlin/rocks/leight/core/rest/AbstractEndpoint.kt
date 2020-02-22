@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package rocks.leight.core.rest

import com.google.gson.JsonSyntaxException
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.Url
import io.ktor.request.header
import io.ktor.response.respond
import io.ktor.routing.*
import mu.KotlinLogging
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.joda.time.DateTime
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.job.IJobManager
import rocks.leight.core.api.rest.IEndpoint
import rocks.leight.core.api.rest.RestException
import rocks.leight.core.api.rest.UnauthorizedException
import rocks.leight.core.api.server.ILinkGenerator
import rocks.leight.core.api.storage.IStorage
import rocks.leight.core.utils.asStamp
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

typealias OrderByPair = Pair<Column<*>, SortOrder>
typealias OrderByMap = Map<String?, OrderByPair>

enum class LinkType {
    ROOT,
    RESOURCE,
    COLLECTION,
    ACTION,
}

class Href(
        val href: String
) {
    constructor(href: Url) : this(href.toString())
}

data class PagesIndex(
        val total: Int,
        val limit: Int,
        val count: Int,
        val hrefs: List<Href>,
        val stamp: String = DateTime().asStamp()
)

data class Link(
        val name: String,
        val type: LinkType,
        val href: String
) {
    constructor(name: String, type: LinkType, href: Url) : this(name, type, href.toString())
}

data class LinkIndex(
        val links: List<Link>
)

abstract class AbstractEndpoint(protected val container: IContainer, val endpoint: String) : IEndpoint {
    protected val storage: IStorage by container.lazy()
    protected val jobManager: IJobManager by container.lazy()
    protected val linkGenerator: ILinkGenerator by container.lazy()
    protected val logger = KotlinLogging.logger(this::class.qualifiedName!!)

    override fun install(routing: Routing) {
        routing.route(endpoint) {
            get { onGet(call) }
            post { onPost(call) }
            patch { onPatch(call) }
            put { onPut(call) }
            delete { onDelete(call) }
        }
        onInstall(routing)
    }

    protected open fun onInstall(routing: Routing) {
    }

    protected open suspend fun onGet(call: ApplicationCall) = notImplemented(call, "Get is not implemented.")

    protected open suspend fun onPost(call: ApplicationCall) = notImplemented(call, "Post is not implemented.")

    protected open suspend fun onPatch(call: ApplicationCall) = notImplemented(call, "Patch is not implemented.")

    protected open suspend fun onPut(call: ApplicationCall) = notImplemented(call, "Put is not implemented.")

    protected open suspend fun onDelete(call: ApplicationCall) = notImplemented(call, "Delete is not implemented.")

    protected fun href(
            path: String,
            parameters: ParametersBuilder.() -> Unit
    ): Url = linkGenerator.href(path, parameters)

    protected fun href(
            path: String,
            parameters: Parameters = Parameters.Empty
    ): Url = linkGenerator.href(path, parameters)

    protected suspend fun badRequest(
            call: ApplicationCall,
            error: String
    ) = call.respond(HttpStatusCode.BadRequest, mapOf("error" to error))

    protected suspend fun forbidden(
            call: ApplicationCall,
            error: String
    ) = call.respond(HttpStatusCode.Forbidden, mapOf("error" to error))

    protected suspend fun unauthorized(
            call: ApplicationCall,
            error: String
    ) = call.respond(HttpStatusCode.Unauthorized, mapOf("error" to error))

    protected suspend fun created(
            call: ApplicationCall,
            href: Url
    ) = call.respond(HttpStatusCode.Created, mapOf("href" to href.toString()))

    protected suspend fun noContent(
            call: ApplicationCall
    ) = call.respond(HttpStatusCode.NoContent)

    protected suspend fun notFound(
            call: ApplicationCall,
            error: String
    ) = call.respond(HttpStatusCode.NotFound, mapOf("error" to error))

    protected suspend fun conflict(
            call: ApplicationCall,
            error: String
    ) = call.respond(HttpStatusCode.Conflict, mapOf("error" to error))

    protected suspend fun notImplemented(
            call: ApplicationCall,
            error: String
    ) = call.respond(HttpStatusCode.NotImplemented, mapOf("error" to error))

    protected suspend fun internalServerError(
            call: ApplicationCall,
            error: String
    ) = call.respond(HttpStatusCode.InternalServerError, mapOf("error" to error))

    protected suspend fun accepted(
            call: ApplicationCall,
            message: String? = null
    ) = if (message != null) {
        call.respond(HttpStatusCode.Accepted, mapOf("message" to message))
    } else {
        call.respond(HttpStatusCode.Accepted)
    }

    suspend fun <T : UUIDEntity> item(
            call: ApplicationCall,
            export: (entity: T) -> Map<String, *>,
            entity: (uuid: UUID) -> T
    ) = try {
        storage.transaction { entity(UUID.fromString(call.parameters["uuid"]!!)) }.let { call.respond(export(it)) }
    } catch (e: IllegalArgumentException) {
        badRequest(call, "Requested UUID does not looks like UUID.")
    } catch (e: EntityNotFoundException) {
        notFound(call, "Requested unknown entity [${call.parameters["uuid"]}].")
    }

    suspend fun paging(
            call: ApplicationCall,
            orderByMap: OrderByMap,
            table: UUIDTable,
            itemHref: String,
            pageHref: String
    ) = if (call.parameters.contains("page")) page(call, itemHref, orderByMap, table) else pages(call, pageHref, table)

    /**
     * respond with page index
     */
    suspend fun pages(
            call: ApplicationCall,
            href: String,
            table: UUIDTable
    ) = call.respond(storage.transaction {
        val total = table.slice(table.id).selectAll().count()
        val limit = if (call.parameters.contains("limit")) call.parameters["limit"]!!.toInt() else 100
        with(mutableListOf<Href>()) {
            repeat(ceil(total.toDouble() / limit.toDouble()).toInt()) { add(Href(href(href.replace("{page}", "$it")))) }
            PagesIndex(
                    total,
                    limit,
                    count(),
                    this
            )
        }
    })

    /**
     * respond with the given page - return item hrefs and timestamp
     */
    suspend fun page(
            call: ApplicationCall,
            orderByMap: OrderByMap,
            count: () -> Int,
            items: (limit: Int, offset: Int, orderByPair: OrderByPair) -> List<ListItem>
    ) {
        try {
            val page = call.parameters["page"]?.toInt() ?: 0
            if (page < 0) {
                return badRequest(call, "Page must be a positive number")
            }
            try {
                val limit = call.parameters["limit"]?.toInt() ?: 100
                if (limit < 5) {
                    return badRequest(call, "Limit must be a positive number and higher than 5")
                }
                if (limit > 100) {
                    return badRequest(call, "Limit cannot be higher than 100")
                }
                call.respond(storage.transaction {
                    val total = count()
                    val pages = floor(total.toDouble() / limit.toDouble()).toInt()
                    if (page > pages) {
                        throw Exception("Out of range: page [$page] cannot be higher than [$pages]")
                    }
                    PageIndex(items(limit, page * limit, orderByMap.getOrElse(call.parameters["order-by"]) { throw RestException("Unsupported order-by parameter [${call.parameters["order-by"]}].") }))
                })
            } catch (e: NumberFormatException) {
                badRequest(call, "Limit must be a number")
            } catch (e: Exception) {
                badRequest(call, e.message ?: "You're making me suffering from huge pain!")
            }
        } catch (e: NumberFormatException) {
            badRequest(call, "Page must be a number")
        }
    }

    /**
     * respond with the given page - return item hrefs and timestamp
     */
    suspend fun page(
            call: ApplicationCall,
            href: String,
            orderByMap: OrderByMap,
            table: UUIDTable
    ) = page(call, orderByMap, { table.slice(table.id).selectAll().count() }, { limit, page, orderByPair ->
        table.slice(table.id).selectAll().orderBy(orderByPair).limit(limit, page).map { ListItem(it[table.id], href(href.replace("{id}", it[table.id].toString()))) }
    })

    suspend fun handle(
            call: ApplicationCall,
            callback: suspend ApplicationCall.() -> Unit
    ) {
        try {
            callback(call)
        } catch (e: JsonSyntaxException) {
            badRequest(call, "Malformed JSON")
            logger.error(e.message, e)
        } catch (e: UnauthorizedException) {
            forbidden(call, "Your request looks not good for us, sorry.")
            logger.error(e.message, e)
        } catch (e: Throwable) {
            internalServerError(call, "Something went wrong")
            logger.error(e.message, e)
        }
    }

    protected fun getUserFromCall(call: ApplicationCall): Any {
        return call.request.header("Authorization")?.let {
            try {
                fetchUserByToken(it.toLowerCase().replace("bearer ", ""))
            } catch (e: Throwable) {
                throw UnauthorizedException("Cannot request user by token", e)
            }
        } ?: throw UnauthorizedException("No authorization header is present")
    }

    protected open fun fetchUserByToken(token: String): Any {
        throw UnauthorizedException("Authorization is not implemented")
    }
}
