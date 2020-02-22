package rocks.leight.core.exposed

import org.jetbrains.exposed.dao.UUIDEntity

fun <T : UUIDEntity> T.toExport(export: T.() -> Array<Pair<*, *>>): HashMap<*, *> = hashMapOf("id" to this.id.toString(), *export())
