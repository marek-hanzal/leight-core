@file:Suppress("unused")

package rocks.leight.core.api.job

import rocks.leight.core.api.CoreException

open class JobException(message: String, cause: Throwable? = null) : CoreException(message, cause)
