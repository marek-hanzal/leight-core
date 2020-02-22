@file:Suppress("UNCHECKED_CAST")

package rocks.leight.core.api.container

import rocks.leight.core.api.config.IConfigurable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class LazyDelegate<T : Any>(val container: IContainer) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val instance = container.create(property.returnType.classifier as KClass<T>)
        if (instance is IConfigurable<*>) {
            instance.setup()
        }
        return instance
    }
}
