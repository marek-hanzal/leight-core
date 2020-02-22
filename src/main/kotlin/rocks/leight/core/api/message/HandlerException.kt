package rocks.leight.core.api.message

open class HandlerException(message: String, cause: Throwable? = null) : MessageException(message, cause)
