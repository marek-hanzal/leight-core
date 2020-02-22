package rocks.leight.core.api.message

import rocks.leight.core.api.CoreException

open class MessageException(message: String, cause: Throwable? = null) : CoreException(message, cause)
