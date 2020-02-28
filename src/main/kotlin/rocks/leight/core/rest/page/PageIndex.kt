package rocks.leight.core.rest.page

import org.joda.time.DateTime
import rocks.leight.core.utils.asStamp

data class PageIndex(
        val items: List<ListItem>,
        val stamp: String = DateTime().asStamp()
)
