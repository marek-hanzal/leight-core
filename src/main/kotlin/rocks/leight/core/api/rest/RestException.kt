package rocks.leight.core.api.rest

import rocks.leight.core.api.CoreException

open class RestException(message: String, cause: Throwable? = null) : CoreException(message, cause)
