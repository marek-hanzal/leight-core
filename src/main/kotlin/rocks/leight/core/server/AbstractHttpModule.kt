@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package rocks.leight.core.server

import io.ktor.routing.Routing
import mu.KotlinLogging
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.rest.IEndpoint
import rocks.leight.core.api.server.IHttpModule
import kotlin.reflect.KClass

abstract class AbstractHttpModule(protected val container: IContainer) : IHttpModule {
    protected val logger = KotlinLogging.logger(this::class.qualifiedName!!)

    protected fun <T : IEndpoint> endpoint(routing: Routing, endpoint: KClass<T>) = container.create(endpoint).install(routing).also {
        logger.debug { "Endpoint: Registered endpoint [${endpoint.qualifiedName}]" }
    }
}
