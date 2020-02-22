@file:Suppress("unused")

package rocks.leight.core.rest

import io.ktor.http.Url
import org.jetbrains.exposed.dao.EntityID
import org.joda.time.DateTime
import rocks.leight.core.utils.asStamp
import java.util.*

data class ListPage(
        val current: Int,
        val count: Int,
        val size: Int,
        val first: String,
        val last: String,
        val previous: String?,
        val next: String?
) {
    constructor(current: Int, count: Int, size: Int, first: Url, last: Url, previous: Url?, next: Url?) : this(current, count, size, first.toString(), last.toString(), previous?.toString(), next?.toString())
}

data class PageIndex(
        val items: List<ListItem>,
        val stamp: String = DateTime().asStamp()
)

data class ListItem(val id: String, val href: String) {
    constructor(id: UUID, href: Url) : this(id.toString(), href.toString())
    constructor(id: EntityID<UUID>, href: Url) : this(id.toString(), href.toString())
}
