package rocks.leight.core.api.message

class MultipleHandlersException(message: String, cause: Throwable? = null) : HandlerException(message, cause)
