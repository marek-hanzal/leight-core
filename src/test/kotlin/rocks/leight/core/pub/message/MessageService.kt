@file:Suppress("unused", "UNUSED_PARAMETER")

package rocks.leight.core.pub.message

import rocks.leight.core.api.message.Handler
import rocks.leight.core.api.message.IMessage
import rocks.leight.core.message.AbstractMessage
import rocks.leight.core.message.AbstractMessageService
import rocks.leight.core.message.SimpleMessage

data class SayHelloMessage(val toWho: String) : AbstractMessage("say-hello")
class DummyMessage(val sleep: Long = 0) : AbstractMessage("dummy")

class MessageService : AbstractMessageService<MessageService>() {
	override fun getTarget(): String? = null

	@Handler("say-hello")
	fun handleSomeEventMessage(message: SayHelloMessage): IMessage? = SayHelloMessage(message.toWho)

	@Handler("dummy")
	fun handleDummy(message: DummyMessage) {
		Thread.sleep(message.sleep)
	}

	@Handler("unit")
	fun handleUnit(message: SimpleMessage) {
	}

	@Handler("boom")
	fun invalidHandler(message: IMessage) {
	}

	@Handler("boom")
	fun mateOfInvalidHandler(message: IMessage): IMessage? = null
}
