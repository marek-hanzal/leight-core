package rocks.leight.core.api.container

import java.util.*
import kotlin.reflect.KClass

interface IFactory<T> {
	fun getUuid(): UUID

	fun getName(): String

	fun getReflection(): Array<KClass<*>>

	fun create(container: IContainer, params: Array<*>? = null): T
}
