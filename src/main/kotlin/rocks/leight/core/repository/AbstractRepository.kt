package rocks.leight.core.repository

import org.jetbrains.exposed.dao.UUIDEntity
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.repository.IRepository
import rocks.leight.core.api.storage.IStorage
import java.util.*

abstract class AbstractRepository<T : UUIDEntity>(container: IContainer) : IRepository<T> {
	protected val storage: IStorage by container.lazy()

	override fun delete(uuid: UUID) = storage.write { getById(uuid).delete() }
}
