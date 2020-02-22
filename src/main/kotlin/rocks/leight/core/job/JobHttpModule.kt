package rocks.leight.core.job

import io.ktor.application.Application
import io.ktor.routing.routing
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.job.rest.StatsEndpoint
import rocks.leight.core.server.AbstractHttpModule

class JobHttpModule(container: IContainer) : AbstractHttpModule(container) {
    override fun install(application: Application) {
        application.routing {
            endpoint(this, StatsEndpoint::class)
        }
    }
}
