package rocks.leight.core.job

import mu.KotlinLogging
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.job.*
import rocks.leight.core.api.message.IMessage
import rocks.leight.core.api.storage.IStorage
import rocks.leight.core.job.entity.Job
import rocks.leight.core.job.entity.JobTable
import rocks.leight.core.utils.asStamp
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class JobManager(container: IContainer) : IJobManager {
	private val storage: IStorage by container.lazy()
	private val jobExecutor: IJobExecutor by container.lazy()
	private val jobScheduler: IJobScheduler by container.lazy()
	private val jobController: IJobController by container.lazy()
	private val executor = Executors.newFixedThreadPool(3)
	private val logger = KotlinLogging.logger { }

	override fun start() {
		logger.debug { "Start: Starting job manager" }
		reset()
		jobExecutor.start()
		executor.execute(jobExecutor)
		executor.execute(jobScheduler)
		executor.execute(jobController)
		logger.debug { "Start: Done" }
	}

	override fun create(message: IMessage, schedule: DateTime, priority: Int) = Job.new {
		if (priority > 999 || priority < 0) {
			throw JobException("Hey, quite invalid priority for me, keep it between [0..999], ok?")
		}
		this.message = message
		this.schedule = schedule
		this.sort = (priority.toString() + Regex("[^0-9]").replace(schedule.asStamp(), "")).padStart(20, '0').substring(0, 20)
	}

	override fun execute(message: IMessage) = create(message).also { jobExecutor.execute(it) }

	override fun schedule(message: IMessage) = create(message)

	override fun schedule(message: IMessage, schedule: DateTime, priority: Int) = create(message, schedule, priority)

	override fun schedule() = jobScheduler.schedule()

	override fun stop(): Boolean {
		logger.debug { "Stop: Stopping" }
		val start = System.currentTimeMillis()
		/**
		 * because executed threads are application-life running jobs, they must be
		 * killed by an interruption
		 */
		executor.shutdownNow()
		val timeout: Long = 5
		var graceful = executor.awaitTermination(timeout, TimeUnit.SECONDS)
		if (!graceful) {
			logger.warn { "Stop: Timeout of ${timeout}s passed when stopping, job scheduler is in unknown state" }
		}
		graceful = jobExecutor.stop() && graceful
		logger.debug { "Stop: Done in ${System.currentTimeMillis() - start}ms" }
		return graceful
	}

	private fun reset() {
		storage.write {
			fun resetFrom(jobState: JobState) {
				JobTable.update({ JobTable.state eq (jobState) }) {
					it[JobTable.state] = JobState.CREATED
					it[JobTable.reset] = true
				}.also {
					if (it > 0) {
						this@JobManager.logger.debug { "Reset: #$it jobs has been reset from [$jobState] state" }
					}
				}
			}
			resetFrom(JobState.SCHEDULED)
			resetFrom(JobState.QUEUED)
			/**
			 * only running jobs should be written as warning, because they may cause repeated action
			 */
			JobTable.update({ JobTable.state eq (JobState.RUNNING) }) {
				it[JobTable.state] = JobState.CREATED
				it[JobTable.reset] = true
			}.also {
				if (it > 0) {
					this@JobManager.logger.warn { "Reset: #$it jobs has been reset from [${JobState.RUNNING}] state" }
				}
			}
		}
	}
}
