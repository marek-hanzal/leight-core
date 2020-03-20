package rocks.leight.core.rest.page

import org.joda.time.DateTime
import rocks.leight.core.rest.Href
import rocks.leight.core.utils.asStamp

data class PagesIndex(
	val total: Int,
	val limit: Int,
	val count: Int,
	val hrefs: List<Href>,
	val stamp: String = DateTime().asStamp()
)
