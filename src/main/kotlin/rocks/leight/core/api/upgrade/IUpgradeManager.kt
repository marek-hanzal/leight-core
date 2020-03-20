package rocks.leight.core.api.upgrade

import rocks.leight.core.api.config.IConfigurable
import kotlin.reflect.KClass

interface IUpgradeManager : IConfigurable<IUpgradeManager> {
	/**
	 * register an upgrade (order of upgrades is important)
	 */
	fun <T : IUpgrade> register(upgrade: KClass<T>): IUpgradeManager

	/**
	 * execute upgrade process
	 */
	fun upgrade(): Int
}
