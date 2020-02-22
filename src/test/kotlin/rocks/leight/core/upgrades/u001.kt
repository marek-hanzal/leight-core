package rocks.leight.core.upgrades

import rocks.leight.core.api.container.IContainer
import rocks.leight.core.upgrade.AbstractUpgrade

class u001(container: IContainer) : AbstractUpgrade(container) {
    private val state: SomeStateClass by container.lazy()

    override fun upgrade() {
        Thread.sleep(50)
        state.upgrades.add(this::class)
    }
}
