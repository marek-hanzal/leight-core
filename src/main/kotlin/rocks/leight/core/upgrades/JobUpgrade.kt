package rocks.leight.core.upgrades

import org.jetbrains.exposed.sql.SchemaUtils
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.job.entity.JobTable
import rocks.leight.core.upgrade.AbstractUpgrade

class JobUpgrade(container: IContainer) : AbstractUpgrade(container) {
    override fun upgrade() = storage.transaction { SchemaUtils.create(JobTable) }
}
