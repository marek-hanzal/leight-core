package rocks.leight.core.storage

import rocks.leight.core.api.CoreException

open class StorageException(message: String, cause: Throwable? = null) : CoreException(message, cause)
