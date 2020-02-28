package rocks.leight.core.api.server

import io.ktor.routing.Routing

interface IHttpModule {
    fun install(routing: Routing)
}
