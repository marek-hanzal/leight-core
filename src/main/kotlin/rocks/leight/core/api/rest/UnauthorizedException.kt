package rocks.leight.core.api.rest

class UnauthorizedException(message: String, cause: Throwable? = null) : RestException(message, cause)
