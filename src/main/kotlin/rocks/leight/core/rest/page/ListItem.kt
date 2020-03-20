@file:Suppress("unused")

package rocks.leight.core.rest.page

import io.ktor.http.Url
import org.jetbrains.exposed.dao.EntityID
import java.util.*

data class ListItem(val id: String, val href: String) {
	constructor(id: UUID, href: Url) : this(id.toString(), href.toString())
	constructor(id: EntityID<UUID>, href: Url) : this(id.toString(), href.toString())
}
