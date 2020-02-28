package rocks.leight.core.job

import mu.KotlinLogging
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.joda.time.DateTime
import org.slf4j.MDC
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.job.IJobExecutor
import rocks.leight.core.api.job.IJobScheduler
import rocks.leight.core.api.storage.IStorage
import rocks.leight.core.job.entity.Job
import rocks.leight.core.job.entity.JobTable
import java.util.concurrent.Semaphore

internal class JobScheduler(container: IContainer) : IJobScheduler {
    private val jobExecutor: IJobExecutor by container.lazy()
    private val jobConfig: JobConfig by container.lazy()
    private val storage: IStorage by container.lazy()
    private val logger = KotlinLogging.logger { }
    private val semaphore = Semaphore(0)

    override fun schedule() {
        logger.debug { "Schedule: Releasing ticket, current tickets #${semaphore.availablePermits()}" }
        if (semaphore.availablePermits() >= jobConfig.scheduleLimit) {
            logger.error { "Schedule: Too many tickets (${semaphore.availablePermits()} of ${jobConfig.scheduleLimit}): the job pipeline is probably stuck!" }
        }
        semaphore.release()
    }

    override fun run() {
        logger.debug { "Run: Running!" }
        try {
            while (!Thread.currentThread().isInterrupted) {
                MDC.remove("jobId")
                logger.debug { if (semaphore.availablePermits() <= 0) "Scheduler: No available tickets, scheduler is idle" else "Scheduler: Loop, scheduler tickets #${semaphore.availablePermits()}" }
                semaphore.acquire()
                logger.debug { "Scheduler: Woke up, scheduling work, remaining tickets #${semaphore.availablePermits()}" }
                if (jobExecutor.count() >= jobConfig.limit) {
                    logger.debug { "Scheduler: Too much work on board (#${jobExecutor.count()} scheduled of #${jobConfig.limit} limit), postponing" }
                    continue
                }
                if (jobExecutor.count() >= jobConfig.queueLimit) {
                    logger.warn { "Scheduler: There are more scheduled jobs (${jobExecutor.count()}) then executor is able to take (limit of ${jobConfig.queueLimit}) - schedule is postponed; are you calling schedule() in a loop? Is job executor healthy?" }
                    continue
                }
                storage.write {
                    Job.find { JobTable.state eq JobState.CREATED and (JobTable.schedule lessEq DateTime()) }.orderBy(JobTable.sort to SortOrder.ASC).limit(jobConfig.limit).toList().map { it.state = JobState.SCHEDULED; it }
                }.let {
                    if (it.count() > 0) {
                        this@JobScheduler.logger.debug { "Scheduler: Job count #${jobExecutor.count()}, new jobs #${it.count()}" }
                        jobExecutor.enqueue(it)
                    } else {
                        this@JobScheduler.logger.debug { "Scheduler: No new jobs available" }
                    }
                }
                logger.debug { "Scheduler: Done, scheduler tickets #${semaphore.availablePermits()}, executor tickets #${jobExecutor.tickets()}, queue size #${jobExecutor.count()}" }
            }
        } catch (e: InterruptedException) {
            logger.debug("Run: Interrupted")
        }
        logger.debug { "Run: Finished" }
    }
}
