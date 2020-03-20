package rocks.leight.core.job

import mu.KotlinLogging
import org.slf4j.MDC
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.job.IJobExecutor
import rocks.leight.core.api.message.IMessageBus
import rocks.leight.core.api.storage.IStorage
import rocks.leight.core.job.entity.Job
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

internal class JobExecutor(container: IContainer) : IJobExecutor {
	private val messageBus: IMessageBus by container.lazy()
	private val storage: IStorage by container.lazy()
	private val jobConfig: JobConfig by container.lazy()
	private val logger = KotlinLogging.logger { }
	private val executorSemaphore = Semaphore(0)
	private val jobSemaphore = Semaphore(jobConfig.limit)
	private val queue = LinkedBlockingQueue<Job>(jobConfig.limit * 4)
	private val executor by lazy {
		ScheduledThreadPoolExecutor(jobConfig.workers).apply {
			removeOnCancelPolicy = true
			continueExistingPeriodicTasksAfterShutdownPolicy = false
			executeExistingDelayedTasksAfterShutdownPolicy = false
		}
	}

	override fun start() {
		logger.debug { "Start: Starting job executor with #${jobConfig.workers} threads, runtime threshold ${jobConfig.runtimeThreshold}ms" }
		logger.debug { "Start: Done!" }
	}

	override fun run() {
		logger.debug { "Run: Running!" }
		try {
			while (!Thread.currentThread().isInterrupted) {
				MDC.remove("jobId")
				logger.debug { if (executorSemaphore.availablePermits() <= 0) "Executor: No available tickets, executor is idle" else "Executor: Loop" }
				executorSemaphore.acquire()
				try {
					queue.poll().let {
						MDC.put("jobId", it.id.toString())
						logger.debug { "Executor: Executing job, remaining tickets #${executorSemaphore.availablePermits()}, queue size #${queue.size}" }
						execute(it)
					}
				} catch (e: IllegalStateException) {
					logger.error { "Executor: There is null in queue, some quite though shit happened; queue size #${queue.size}, ticket count #${executorSemaphore.availablePermits()}!" }
				}
				logger.debug { "Executor: Done, remaining tickets #${executorSemaphore.availablePermits()}, queue size #${queue.size}" }
			}
		} catch (e: InterruptedException) {
			logger.debug("Run: Interrupted")
		}
		logger.debug { "Run: Finished" }
	}

	override fun enqueue(jobs: List<Job>) {
		logger.debug { "Enqueue: Adding #${jobs.count()} new jobs" }
		queue.addAll(jobs)
		logger.debug { "Enqueue: Releasing #${jobs.count()} new tickets" }
		executorSemaphore.release(jobs.count())
		logger.debug { "Enqueue: Enqueued #${jobs.count()} new items, current queue size #${queue.count()}, ticket count #${executorSemaphore.availablePermits()}" }
	}

	override fun execute(jobEntity: Job) {
		MDC.put("jobId", jobEntity.id.toString())
		fun commit(start: Long, jobEntity: Job, jobState: JobState) {
			val runtime = (System.currentTimeMillis() - start).toInt()
			jobEntity.state = jobState
			if (jobConfig.runtimeThreshold in 1..(runtime - 1)) {
				logger.warn { "Execute: Runtime (${runtime}ms) more than ${jobConfig.runtimeThreshold}ms" }
			}
			logger.debug { "Execute: Done in ${runtime}ms" }
		}
		if (jobEntity.reset) {
			logger.warn { "Execute: Job has been reset from the original [running] state!" }
		} else {
			logger.debug { "Execute: Starting" }
		}
		if (jobEntity.timeout <= -jobConfig.lateJobThreshold) {
			logger.warn { "Execute: Found a job with late execution of ${-1 * jobEntity.timeout}ms" }
		}
		if (jobSemaphore.availablePermits() <= 0) {
			logger.debug { "Execute: A lot of running jobs (~${executor.queue.count()} of ${jobConfig.limit}), executor will block" }
		}
		jobSemaphore.acquire()
		logger.debug { "Execute: Started, running jobs #${count()}, queue size #${executor.queue.count()}" }
		storage.write { jobEntity.state = JobState.QUEUED }
		executor.execute {
			MDC.put("jobId", jobEntity.id.toString())
			logger.debug { "Execute: Started" }
			val start = System.currentTimeMillis()
			storage.write { jobEntity.state = JobState.RUNNING }
			try {
				messageBus.fire(jobEntity.message)
				storage.write { commit(start, jobEntity, JobState.SUCCESSFUL) }
			} catch (e: Throwable) {
				logger.error("Execute: Failed", e)
				StringWriter().let { writer ->
					e.cause?.printStackTrace(PrintWriter(writer))
					storage.write { commit(start, jobEntity, JobState.FAILED); jobEntity.error = writer.toString() }
				}
			} finally {
				logger.debug { "Execute: Done, running jobs #${count()} (includes self), planned jobs #${executor.queue.count()}" }
				jobSemaphore.release()
			}
		}
	}

	override fun current(): Int = executor.activeCount

	override fun count(): Int = queue.size

	override fun tickets(): Int = executorSemaphore.availablePermits()

	override fun stop(): Boolean {
		logger.debug { "Stop: Stopping" }
		val start = System.currentTimeMillis()
		executor.shutdown()
		val graceful = executor.awaitTermination(30, TimeUnit.SECONDS)
		if (!graceful) {
			logger.warn { "Stop: Timeout of 30s passed when stopping, forcing now" }
			executor.shutdownNow()
			if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
				logger.error { "Stop: Forced shutdown failed on timeout of 30s" }
			}
		}
		logger.debug { "Stop: Done ${System.currentTimeMillis() - start}ms" }
		return graceful
	}
}
