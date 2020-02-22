package rocks.leight.core.job

import mu.KotlinLogging
import org.jetbrains.exposed.sql.SortOrder
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.job.IJobController
import rocks.leight.core.api.job.IJobScheduler
import rocks.leight.core.api.storage.IStorage
import java.util.*
import kotlin.math.max
import kotlin.math.min

internal class JobController(container: IContainer) : IJobController {
    private val jobScheduler: IJobScheduler by container.lazy()
    private val jobConfig: JobConfig by container.lazy()
    private val storage: IStorage by container.lazy()
    private val logger = KotlinLogging.logger { }

    override fun run() {
        logger.debug { "Run: Running!" }
        try {
            while (!Thread.currentThread().isInterrupted) {
                logger.debug { "Run: Ordering schedule ticket" }
                jobScheduler.schedule()
                Thread.sleep(storage.read {
                    try {
                        max(jobConfig.shallowSleep, min(jobConfig.deepSleep, JobEntity.find { JobTable.state eq JobState.CREATED }.orderBy(JobTable.schedule to SortOrder.ASC).limit(1).first().timeout))
                    } catch (e: NoSuchElementException) {
                        this@JobController.logger.debug { "Run: No more jobs, going to deep sleep for ${jobConfig.deepSleep}ms" }
                        jobConfig.deepSleep
                    }
                }.also { logger.debug { "Run: Falling asleep for ${it}ms" } })
            }
        } catch (e: InterruptedException) {
            logger.debug("Run: Interrupted")
        }
        logger.debug { "Run: Finished" }
    }
}
