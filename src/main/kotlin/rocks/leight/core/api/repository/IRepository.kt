package rocks.leight.core.api.repository

import org.jetbrains.exposed.dao.UUIDEntity
import java.util.*

interface IRepository<T : UUIDEntity> {
    fun update(uuid: String, update: Any) = update(getById(uuid), update)

    fun getById(uuid: String) = getById(UUID.fromString(uuid))

    fun delete(uuid: String) = delete(UUID.fromString(uuid))

    fun delete(uuid: UUID)

    fun getById(uuid: UUID): T

    fun update(entity: T, update: Any)
}
