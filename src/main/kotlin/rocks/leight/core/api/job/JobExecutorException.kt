@file:Suppress("unused")

package rocks.leight.core.api.job

open class JobExecutorException(message: String, cause: Throwable? = null) : JobException(message, cause)
