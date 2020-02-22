package rocks.leight.core.storage

class DuplicateEntityException(message: String, cause: Throwable? = null) : StorageException(message, cause)
