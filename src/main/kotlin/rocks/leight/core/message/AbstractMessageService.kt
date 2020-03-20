@file:Suppress("UNCHECKED_CAST")

package rocks.leight.core.message

import rocks.leight.core.api.message.*
import rocks.leight.core.config.AbstractConfigurable
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

abstract class AbstractMessageService<T : IMessageService<*>> : AbstractConfigurable<T>(), IMessageService<T> {
	override fun <TResponse : IMessage> execute(message: IMessage) = this@AbstractMessageService::class.functions.filter { func -> (func.returnType.classifier == Unit::class || func.returnType.classifier == IMessage::class) && func.findAnnotation<Handler>()?.type == message.type }.let { functions ->
		val service = this@AbstractMessageService::class.qualifiedName
		when (functions.count()) {
			0 -> throw NoHandlerException("Cannot handle message type [${message.type}] as there is no handler of this type in message service [$service]; method can return (nullable) [${IMessage::class.qualifiedName}].")
			1 -> with(functions[0].call(this@AbstractMessageService, message)) { if (functions[0].returnType.classifier == Unit::class) null else this as TResponse? }
			else -> throw MultipleHandlersException("Cannot handle message type [${message.type}] as there are more handlers [${functions.joinToString(", ") { it.name }}] in message service [$service].")
		}
	}
}
