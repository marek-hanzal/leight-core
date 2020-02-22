@file:Suppress("unused", "UNCHECKED_CAST")

package rocks.leight.core.exposed

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import rocks.leight.core.api.CoreException
import rocks.leight.core.api.message.IMessage
import java.io.InputStream
import java.sql.Clob
import java.sql.PreparedStatement

data class JsonMessage(val klazz: String, val message: String)

fun Table.jsonb(name: String): Column<IMessage> = registerColumn(name, object : ColumnType() {
    override fun sqlType() = "text"

    override fun setParameter(stmt: PreparedStatement, index: Int, value: Any?) = stmt.setObject(index, value)

    override fun valueFromDB(value: Any): Any = when (value) {
        is IMessage -> value
        is Clob -> klaxon(value.asciiStream)
        is String -> klaxon(value.byteInputStream())
        else -> throw CoreException("Cannot convert unsupported value from database value [${value::class.qualifiedName}]")
    }

    override fun notNullValueToDB(value: Any): Any = message(value)

    override fun nonNullValueToString(value: Any): String = "'${message(value)}'"

    private fun klaxon(stream: InputStream): IMessage = Klaxon().run {
        val jsonMessage = fromJsonObject(parser(JsonMessage::class).parse(stream) as JsonObject, JsonMessage::class.java, JsonMessage::class) as JsonMessage
        val klazz = Class.forName(jsonMessage.klazz).kotlin
        fromJsonObject(parser(klazz).parse(jsonMessage.message.byteInputStream()) as JsonObject, klazz.java, klazz) as IMessage
    }

    private fun message(value: Any) = Klaxon().run {
        toJsonString(JsonMessage(value::class.qualifiedName!!, toJsonString(value)))
    }
})
