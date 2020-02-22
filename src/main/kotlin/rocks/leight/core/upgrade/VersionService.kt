package rocks.leight.core.upgrade

import org.jetbrains.exposed.sql.SchemaUtils
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.storage.IStorage
import rocks.leight.core.api.upgrade.IUpgrade
import rocks.leight.core.api.upgrade.IVersionService
import rocks.leight.core.config.AbstractConfigurable
import rocks.leight.core.utils.asStamp

class VersionService(container: IContainer) : AbstractConfigurable<IVersionService>(), IVersionService {
    private val storage: IStorage by container.lazy()

    override fun getVersion(): String? = try {
        getCollection().first().version
    } catch (e: Throwable) {
        null
    }

    override fun upgrade(upgrade: IUpgrade) = storage.write {
        UpgradeEntity.new {
            version = upgrade.getVersion()
        }
    }

    override fun getCollection() = storage.read { UpgradeEntity.all().sortedByDescending { it.stamp }.iterator().asSequence().asIterable() }

    override fun print() = storage.read { getCollection().forEach { println("\t[${it.stamp.asStamp()}]: ${it.version}") } }

    override fun onSetup() = storage.transaction { SchemaUtils.create(UpgradeTable) }
}
