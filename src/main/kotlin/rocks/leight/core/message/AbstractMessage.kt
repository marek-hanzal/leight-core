package rocks.leight.core.message

import rocks.leight.core.api.message.IMessage

abstract class AbstractMessage(override val type: String, override val target: String? = null) : IMessage
