package rocks.leight.core.storage

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.pool.IPool
import rocks.leight.core.api.storage.IStorage
import rocks.leight.core.config.AbstractConfigurable

class Storage(container: IContainer) : AbstractConfigurable<IStorage>(), IStorage {
	private val pool: IPool by container.lazy()
	private lateinit var database: Database

	override fun <T> transaction(statement: Transaction.() -> T): T {
		return org.jetbrains.exposed.sql.transactions.transaction(this.database, statement)
	}

	override fun onSetup() {
		database = Database.connect(pool.source())
	}
}
