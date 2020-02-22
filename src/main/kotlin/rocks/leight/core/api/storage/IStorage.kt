package rocks.leight.core.api.storage

import org.jetbrains.exposed.sql.Transaction
import rocks.leight.core.api.config.IConfigurable

interface IStorage : IConfigurable<IStorage> {
    fun <T> transaction(statement: Transaction.() -> T): T

    fun <T> read(statement: Transaction.() -> T) = transaction(statement)

    fun <T> write(statement: Transaction.() -> T) = transaction(statement)
}
