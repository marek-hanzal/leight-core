package rocks.leight.core.job

import io.ktor.routing.Routing
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.server.AbstractHttpModule

class JobHttpModule(container: IContainer) : AbstractHttpModule(container) {
	override fun install(routing: Routing) {
	}
}
