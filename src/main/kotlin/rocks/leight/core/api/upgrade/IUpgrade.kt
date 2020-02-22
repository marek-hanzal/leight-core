package rocks.leight.core.api.upgrade

import kotlin.reflect.KClass

interface IUpgrade {
    fun getVersion(): String

    /**
     * is the given upgrade (version) current? That mean run in current upgrade
     * session (could be used for conditional upgrades, usually in situation
     * where older upgrade has done future upgrade's action (create table + alter
     * table)
     */
    fun <T : IUpgrade> isCurrent(upgrade: KClass<T>): Boolean

    /**
     * opposite to [isCurrent] - if one does not want to use *not* operator
     */
    fun <T : IUpgrade> isInstalled(upgrade: KClass<T>): Boolean {
        return !isCurrent(upgrade)
    }

    fun upgrade()

    fun upgrade(upgrades: List<KClass<out IUpgrade>>)
}
