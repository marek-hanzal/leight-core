package rocks.leight.core.upgrades

import rocks.leight.core.api.container.IContainer
import rocks.leight.core.upgrade.AbstractUpgrade

class u003(container: IContainer) : AbstractUpgrade(container) {
	private val state: SomeStateClass by container.lazy()

	override fun upgrade() {
		Thread.sleep(50)
		state.upgrades.add(this::class)
		state.up1 = isInstalled(u001::class)
		state.up2 = isCurrent(u002::class)
	}
}
