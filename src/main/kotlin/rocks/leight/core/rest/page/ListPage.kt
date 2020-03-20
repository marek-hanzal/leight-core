@file:Suppress("unused")

package rocks.leight.core.rest.page

import io.ktor.http.Url

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
