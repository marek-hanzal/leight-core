package rocks.leight.core.server

import rocks.leight.core.api.server.ILinkGenerator
import rocks.leight.core.container.ContainerFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.time.ExperimentalTime

@ExperimentalTime
class LinkGeneratorTest {
	@Test
	fun `container support`() {
		val container = ContainerFactory.container()
		assertSame(container.create(ILinkGenerator::class), container.create(ILinkGenerator::class))
	}

	@Test
	fun `url generator`() {
		ContainerFactory.container().apply {
			register(HttpServerConfig::class) {
				HttpServerConfig(1234, "https://temny-svet:4321")
			}
			create(ILinkGenerator::class).apply {
				assertEquals("https://temny-svet:4321/chlupat%C3%A1+p%C3%AD%C4%8Da", encoded("chlupatá píča").toString())
				assertEquals("https://temny-svet:4321/chlupat%C3%A1+p%C3%AD%C4%8Da?ahoj=true&foo=bar", encoded("chlupatá píča") { set("ahoj", "true"); set("foo", "bar") }.toString())
				assertEquals("https://temny-svet:4321/abc/cde/fga a/q?kopa=hoven", link("abc/cde/fga a/q") { set("kopa", "hoven") }.toString())
				assertEquals("https://temny-svet:4321/abc/cde/fga a/q", link("abc/cde/fga a/q").toString())
			}
		}
	}
}
