@file:Suppress("MemberVisibilityCanBePrivate")

package rocks.leight.core.server

import mu.KotlinLogging
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.server.IHttpModule

abstract class AbstractHttpModule(protected val container: IContainer) : IHttpModule {
	protected val logger = KotlinLogging.logger(this::class.qualifiedName!!)
}
