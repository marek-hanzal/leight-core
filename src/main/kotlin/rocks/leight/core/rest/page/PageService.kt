package rocks.leight.core.rest.page

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.sql.selectAll
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.rest.RestException
import rocks.leight.core.api.rest.page.IPageService
import rocks.leight.core.api.rest.page.OrderByMap
import rocks.leight.core.api.rest.page.OrderByPair
import rocks.leight.core.api.server.ILinkGenerator
import rocks.leight.core.api.storage.IStorage
import rocks.leight.core.rest.Href
import rocks.leight.core.rest.badRequest
import kotlin.math.ceil
import kotlin.math.floor

class PageService(container: IContainer) : IPageService {
    private val storage: IStorage by container.lazy()
    private val linkGenerator: ILinkGenerator by container.lazy()

    override suspend fun index(
            call: ApplicationCall,
            href: String,
            table: UUIDTable
    ) = call.respond(storage.transaction {
        val total = table.slice(table.id).selectAll().count()
        val limit = if (call.parameters.contains("limit")) call.parameters["limit"]!!.toInt() else 100
        with(mutableListOf<Href>()) {
            repeat(ceil(total.toDouble() / limit.toDouble()).toInt()) { add(Href(linkGenerator.link(href.replace("{page}", "$it")))) }
            PagesIndex(
                    total,
                    limit,
                    count(),
                    this
            )
        }
    })

    override suspend fun page(
            call: ApplicationCall,
            orderByMap: OrderByMap,
            count: () -> Int,
            items: (limit: Int, offset: Int, orderByPair: OrderByPair) -> List<ListItem>
    ) {
        try {
            val page = call.parameters["page"]?.toInt() ?: 0
            if (page < 0) {
                return call.badRequest("Page must be a positive number")
            }
            try {
                val limit = call.parameters["limit"]?.toInt() ?: 100
                if (limit < 5) {
                    return call.badRequest("Limit must be a positive number and higher than 5")
                }
                if (limit > 100) {
                    return call.badRequest("Limit cannot be higher than 100")
                }
                call.respond(storage.transaction {
                    val total = count()
                    val pages = floor(total.toDouble() / limit.toDouble()).toInt()
                    if (page > pages) {
                        throw Exception("Out of range: page [$page] cannot be higher than [$pages]")
                    }
                    PageIndex(items(limit, page * limit, orderByMap.getOrElse(call.parameters["order-by"]) {
                        throw RestException("Unsupported order-by parameter [${call.parameters["order-by"]}].")
                    }))
                })
            } catch (e: NumberFormatException) {
                call.badRequest("Limit must be a number")
            } catch (e: Exception) {
                call.badRequest(e.message ?: "You're making me suffering from huge pain!")
            }
        } catch (e: NumberFormatException) {
            call.badRequest("Page must be a number")
        }
    }

    override suspend fun page(
            call: ApplicationCall,
            href: String,
            orderByMap: OrderByMap,
            table: UUIDTable
    ) = page(call, orderByMap, { table.slice(table.id).selectAll().count() }, { limit, page, orderByPair ->
        table.slice(table.id).selectAll().orderBy(orderByPair).limit(limit, page).map { ListItem(it[table.id], linkGenerator.link(href.replace("{id}", it[table.id].toString()))) }
    })
}
