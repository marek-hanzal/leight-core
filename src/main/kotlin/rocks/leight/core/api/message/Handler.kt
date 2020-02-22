package rocks.leight.core.api.message

@Target(AnnotationTarget.FUNCTION)
annotation class Handler(val type: String)
