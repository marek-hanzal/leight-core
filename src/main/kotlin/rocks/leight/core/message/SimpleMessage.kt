package rocks.leight.core.message

data class SimpleMessage(override val type: String, override val target: String? = null) : AbstractMessage(type, target)
