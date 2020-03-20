package rocks.leight.core.api.message

import kotlin.reflect.KClass

interface IMessageBus : IMessageService<IMessageBus> {
	/**
	 * register common service (handling messages without target specified)
	 */
	fun <T : IMessageService<*>> register(messageService: KClass<T>): T
}
