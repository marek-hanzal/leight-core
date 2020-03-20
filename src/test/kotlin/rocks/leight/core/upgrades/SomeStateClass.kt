package rocks.leight.core.upgrades

import rocks.leight.core.api.upgrade.IUpgrade
import kotlin.reflect.KClass

class SomeStateClass {
	val upgrades: MutableList<KClass<out IUpgrade>> = mutableListOf()
	var up1 = false
	var up2 = false
}


