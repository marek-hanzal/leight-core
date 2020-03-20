package rocks.leight.core.container

import rocks.leight.core.api.container.IContainer
import kotlin.reflect.KClass

class LazyProxy<T : Any>(clazz: KClass<T>, container: IContainer) {
	val instance: T by lazy {
		container.create(clazz)
	}
}
