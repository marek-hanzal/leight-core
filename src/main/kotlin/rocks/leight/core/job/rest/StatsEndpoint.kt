package rocks.leight.core.job.rest

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.job.IJobStats
import rocks.leight.core.rest.AbstractEndpoint

class StatsEndpoint(container: IContainer) : AbstractEndpoint(container, "/job/stats") {
    private val jobStats: IJobStats by container.lazy()

    override suspend fun onGet(call: ApplicationCall) {
        try {
            call.respond(jobStats.stats())
        } catch (e: Throwable) {
            logger.error("Cannot compute job stats!", e)
            internalServerError(call, "Cannot compute stats, there is some deep-shit inside :(")
        }
    }
}
