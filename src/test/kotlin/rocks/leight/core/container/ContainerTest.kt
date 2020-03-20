package rocks.leight.core.container

import rocks.leight.core.api.config.IConfigurable
import rocks.leight.core.config.AbstractConfigurable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

class WannaBeConfigured : AbstractConfigurable<WannaBeConfigured>() {
	var value: String = "nope"

	fun gimmeSommeValue(value: String) {
		this.value = value
	}
}

interface InterfaceBro : IConfigurable<InterfaceBro> {
	fun papej(co: String)

	fun vyserHovno(): String
}

class InterfacedThing : AbstractConfigurable<InterfaceBro>(), InterfaceBro {
	private var zaludek: String = "prazdny zaludek"

	override fun papej(co: String) {
		zaludek = co
	}

	override fun vyserHovno() = zaludek
}

class ContainerTest {
	@ExperimentalTime
	@Test
	fun `configurator test`() {
		ContainerFactory.container().apply {
			register(InterfaceBro::class, InterfacedThing::class)
			configurator(InterfaceBro::class) {
				papej("hovno")
			}
			configurator(WannaBeConfigured::class) {
				gimmeSommeValue("yapee!")
			}
			create(WannaBeConfigured::class).apply {
				assertEquals("yapee!", value)
			}
			create(InterfaceBro::class).apply {
				assertEquals("hovno", vyserHovno())
			}
		}
	}
}
