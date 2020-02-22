package rocks.leight.core.api.message

class NoHandlerException(message: String, cause: Throwable? = null) : HandlerException(message, cause)
