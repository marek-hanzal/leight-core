package rocks.leight.core.job

import rocks.leight.core.api.job.IJobManager
import rocks.leight.core.container.ContainerFactory
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.time.ExperimentalTime

class JobManagerTest {
	@ExperimentalTime
	@Test
	fun `container support`() {
		val container = ContainerFactory.container()
		assertSame(container.create(IJobManager::class), container.create(IJobManager::class))
	}
}
