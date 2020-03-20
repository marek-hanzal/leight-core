package rocks.leight.core.api.rest

import io.ktor.routing.Routing

interface IEndpoint {
	fun install(routing: Routing)
}
