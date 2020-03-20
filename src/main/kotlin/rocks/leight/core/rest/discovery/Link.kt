package rocks.leight.core.rest.discovery

import io.ktor.http.Url

data class Link(
	val name: String,
	val link: String,
	val description: String,
	val parameters: List<Parameter> = listOf()
) {
	constructor(
		name: String,
		link: Url,
		description: String,
		parameters: List<Parameter> = listOf()
	) : this(
		name,
		link.toString(),
		description,
		parameters
	)
}
