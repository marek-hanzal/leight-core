@file:Suppress("RemoveExplicitTypeArguments")

package rocks.leight.core.message

import com.beust.klaxon.Klaxon
import rocks.leight.core.api.message.IMessageBus
import rocks.leight.core.api.message.MessageException
import rocks.leight.core.api.message.MultipleHandlersException
import rocks.leight.core.container.ContainerFactory
import rocks.leight.core.pub.message.MessageService
import rocks.leight.core.pub.message.SayHelloMessage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MessageBusTest {
	@Test
	fun `message serialization`() {
		val message = SimpleMessage("foo", "bar")
		assertEquals(message, Klaxon().parse<SimpleMessage>(Klaxon().toJsonString(message)))
	}

	@Test
	fun `test container support`() {
		val container = ContainerFactory.container()
		assertSame(container.create(IMessageBus::class), container.create(IMessageBus::class))
	}

	@Test
	fun `test kaboom on empty MessageBus and empty target`() {
		assertFailsWith<MessageException> {
			ContainerFactory.container().create(IMessageBus::class).apply {
				fire(SimpleMessage("boom"))
			}
		}
	}

	@Test
	fun `test kaboom on empty MessageBus with target specified`() {
		assertFailsWith<MessageException> {
			ContainerFactory.container().create(IMessageBus::class).apply {
				fire(SimpleMessage("boom", "da target"))
			}
		}
	}

	@Test
	fun `test common service`() {
		ContainerFactory.container().create(IMessageBus::class).apply {
			configurator { register(MessageService::class) }
			setup()
			assertEquals(
				"World!",
				execute<SayHelloMessage>(SayHelloMessage("World!"))?.toWho
			)
			assertEquals("to me", execute<SayHelloMessage>(SayHelloMessage("to me"))?.toWho)
		}
	}

	@Test
	fun `test duplicate message handlers in one message service`() {
		ContainerFactory.container().create(IMessageBus::class).apply {
			configurator { register(MessageService::class) }
			setup()
			assertFailsWith<MultipleHandlersException> {
				fire(SimpleMessage("boom"))
			}.also {
				assertEquals(
					"Cannot handle message type [boom] as there are more handlers [invalidHandler, mateOfInvalidHandler] in message service [rocks.leight.core.pub.message.MessageService].",
					it.message
				)
			}
		}

	}
}
