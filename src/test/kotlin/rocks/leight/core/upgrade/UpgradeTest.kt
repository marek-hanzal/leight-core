package rocks.leight.core.upgrade

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import org.jetbrains.exposed.sql.SchemaUtils
import rocks.leight.core.api.storage.IStorage
import rocks.leight.core.api.upgrade.IUpgrade
import rocks.leight.core.api.upgrade.IUpgradeManager
import rocks.leight.core.api.upgrade.IVersionService
import rocks.leight.core.container.ContainerFactory
import rocks.leight.core.pool.PoolConfig
import rocks.leight.core.upgrades.SomeStateClass
import rocks.leight.core.upgrades.u001
import rocks.leight.core.upgrades.u002
import rocks.leight.core.upgrades.u003
import kotlin.reflect.KClass
import kotlin.test.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
class UpgradeTest {
    @Test
    fun `create by a Container`() {
        val container = ContainerFactory.container()
        assertSame(container.create(IUpgradeManager::class), container.create(IUpgradeManager::class))
    }

    @Test
    fun `empty version works`() {
        assertNull(ContainerFactory.container().create(IVersionService::class).getVersion())
    }

    @Test
    fun `exclude already installed upgrade`() {
        ContainerFactory.container().apply {
            register(PoolConfig::class) {
                ConfigFactory.load().extract("core.pool")
            }
            configurator(IUpgradeManager::class) {
                register(u001::class)
                register(u002::class)
                register(u003::class)
            }
            create(IStorage::class).apply { setup() }.transaction {
                SchemaUtils.drop(UpgradeTable)
            }
            create(IVersionService::class).apply { setup(); upgrade(create(u001::class)) }.also { versionService ->
                with(create(IUpgradeManager::class)) {
                    setup()
                    upgrade()
                }
                assertEquals(listOf<KClass<out IUpgrade>>(u002::class, u003::class), create(SomeStateClass::class).upgrades)
                assertEquals(u003::class.qualifiedName, versionService.getVersion())
            }
        }
    }

    @Test
    fun `conditional upgrade`() {
        ContainerFactory.container().apply {
            register(PoolConfig::class) {
                ConfigFactory.load().extract("core.pool")
            }
            configurator(IUpgradeManager::class) {
                register(u001::class)
                register(u002::class)
                register(u003::class)
            }
            create(IStorage::class).apply { setup() }.transaction {
                SchemaUtils.drop(UpgradeTable)
            }
            create(IVersionService::class).apply { setup() }.upgrade(create(u001::class))
            with(create(IUpgradeManager::class)) {
                setup()
                upgrade()
            }
            with(create(SomeStateClass::class)) {
                assertTrue(up1) // isInstalled
                assertTrue(up2) // isCurrent
            }
        }
    }
}
