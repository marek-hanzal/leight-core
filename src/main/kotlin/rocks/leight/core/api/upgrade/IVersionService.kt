package rocks.leight.core.api.upgrade

import rocks.leight.core.api.config.IConfigurable
import rocks.leight.core.upgrade.UpgradeEntity

interface IVersionService : IConfigurable<IVersionService> {
	/**
	 * return current version or null if an application is in zero state
	 */
	fun getVersion(): String?

	/**
	 * add a new upgrade
	 */
	fun upgrade(upgrade: IUpgrade): UpgradeEntity

	/**
	 * return collection of installed upgrades
	 */
	fun getCollection(): Iterable<UpgradeEntity>

	/**
	 * print currently installed versions (version per line)
	 */
	fun print()
}
