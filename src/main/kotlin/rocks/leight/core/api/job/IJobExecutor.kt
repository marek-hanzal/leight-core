package rocks.leight.core.api.job

import rocks.leight.core.job.JobEntity

internal interface IJobExecutor : Runnable {
    fun start()

    fun enqueue(jobs: List<JobEntity>)

    /**
     * block if the execution queue is full (await job execution)
     */
    fun execute(jobEntity: JobEntity)

    /**
     * count of active threads (approx)
     */
    fun current(): Int

    fun count(): Int

    fun tickets(): Int

    fun stop(): Boolean
}
