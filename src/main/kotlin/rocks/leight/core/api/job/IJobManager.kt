@file:Suppress("unused")

package rocks.leight.core.api.job

import org.joda.time.DateTime
import rocks.leight.core.api.message.IMessage
import rocks.leight.core.job.JobEntity

interface IJobManager {
    fun start()

    /**
     * just create a new job; lower priority, higher is a job order (executes first)
     */
    fun create(message: IMessage, schedule: DateTime = DateTime(), priority: Int = 250): JobEntity

    /**
     * executes a message and blocks until it's actually scheduled for execution
     */
    fun execute(message: IMessage): JobEntity

    /**
     * just schedule a message (job schedule should pick it up on the right time)
     */
    fun schedule(message: IMessage): JobEntity

    fun scheduleAll(messages: List<IMessage>, priority: Int = 100) = mutableListOf<JobEntity>().also { var index = 0; messages.forEach { schedule(it, 100 * index++, priority) } }

    /**
     * just schedule a message (job schedule should pick it up on the right time)
     */
    fun schedule(message: IMessage, schedule: DateTime = DateTime(), priority: Int = 250): JobEntity

    fun schedule(message: IMessage, delay: Int, priority: Int = 100) = schedule(message, DateTime().plusMillis(delay), priority)

    /**
     * kick scheduler to take up some jobs and execute them
     */
    fun schedule()

    fun stop(): Boolean
}
