package rocks.leight.core.storage

class EntityNotFoundException(message: String, cause: Throwable? = null) : StorageException(message, cause)
