@file:Suppress("MemberVisibilityCanBePrivate")

package rocks.leight.core.server

import com.google.gson.JsonSyntaxException
import io.ktor.application.ApplicationCall
import mu.KotlinLogging
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.rest.InvalidRequestException
import rocks.leight.core.api.rest.UnauthorizedException
import rocks.leight.core.api.rest.discovery.IDiscoveryService
import rocks.leight.core.api.rest.page.IPageService
import rocks.leight.core.api.server.IHttpModule
import rocks.leight.core.rest.badRequest
import rocks.leight.core.rest.discovery.Parameter
import rocks.leight.core.rest.forbidden
import rocks.leight.core.rest.internalServerError

abstract class AbstractHttpModule(protected val container: IContainer) : IHttpModule {
    protected val discoveryService: IDiscoveryService by container.lazy()
    protected val pageService: IPageService by container.lazy()
    protected val logger = KotlinLogging.logger(this::class.qualifiedName!!)

    protected fun discovery(name: String, path: String, description: String, parameters: List<Parameter> = listOf()) = discoveryService.register(name, path, description, parameters)

    suspend fun handle(
            call: ApplicationCall,
            callback: suspend ApplicationCall.() -> Unit
    ) {
        try {
            callback(call)
        } catch (e: JsonSyntaxException) {
            call.badRequest("Malformed JSON")
            logger.error(e.message, e)
        } catch (e: InvalidRequestException) {
            call.badRequest(e.message ?: "You sent something strange and I don't understand your request. Try read docs, make a coffee or fix this bug :)")
            logger.error(e.message, e)
        } catch (e: UnauthorizedException) {
            call.forbidden("Your request looks not good for us, sorry.")
            logger.error(e.message, e)
        } catch (e: Throwable) {
            call.internalServerError("Something went wrong")
            logger.error(e.message, e)
        }
    }
}
