package rocks.leight.core.upgrade

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.UUIDTable
import org.joda.time.DateTime
import java.util.*

object UpgradeTable : UUIDTable("upgrade") {
	val version = varchar("version", 64).uniqueIndex()
	val stamp = datetime("stamp").clientDefault { DateTime() }
}

class UpgradeEntity(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<UpgradeEntity>(UpgradeTable)

	var version by UpgradeTable.version
	var stamp by UpgradeTable.stamp
}
