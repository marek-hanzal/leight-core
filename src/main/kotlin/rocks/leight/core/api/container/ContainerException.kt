package rocks.leight.core.api.container

import rocks.leight.core.api.CoreException

open class ContainerException(message: String, cause: Throwable? = null) : CoreException(message, cause)
