@file:Suppress("UNCHECKED_CAST")

package rocks.leight.core.container

import rocks.leight.core.api.container.FactoryException
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.container.IFactory
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

abstract class AbstractFactory<U> : IFactory<U> {
    private val uuid: UUID = UUID.randomUUID()

    override fun getUuid(): UUID {
        return uuid
    }
}

open class SingletonFactory<T : Any>(private val impl: KClass<out T>, var instance: T? = null) : AbstractFactory<T>() {
    override fun getName(): String {
        return impl.qualifiedName as String
    }

    override fun getReflection(): Array<KClass<*>> {
        if (impl.primaryConstructor == null) {
            throw FactoryException("Cannot get primary constructor of class [${impl.qualifiedName}].")
        }
        var params = arrayOf<KClass<*>>()
        for (parameter in impl.primaryConstructor!!.parameters) {
            params += parameter.type.classifier as KClass<T>
        }
        return params
    }

    override fun create(container: IContainer, params: Array<*>?): T {
        if (instance == null) {
            if (impl.primaryConstructor == null) {
                throw FactoryException("Cannot get primary constructor of class [${impl.qualifiedName}].")
            }
            var dependencies = params
            if (dependencies == null) {
                dependencies = arrayOf<Any>()
                for (clazz in this.getReflection()) {
                    dependencies += container.create(clazz)
                }
            }
            instance = impl.primaryConstructor!!.call(*dependencies)
        }
        return instance as T
    }
}

class InterfaceFactory<T : Any, U : T>(private val iface: KClass<T>, impl: KClass<out U>, instance: U? = null) :
        SingletonFactory<U>(impl, instance) {
    override fun getName(): String {
        return iface.qualifiedName as String
    }
}

class ContainerCallbackFactory<T : Any, U : T>(
        private val iface: KClass<T>,
        private val callback: IContainer.() -> U,
        impl: KClass<out U>,
        instance: U? = null
) : SingletonFactory<U>(impl, instance) {
    override fun getName(): String {
        return iface.qualifiedName as String
    }

    override fun create(container: IContainer, params: Array<*>?): U {
        if (instance == null) {
            instance = callback.invoke(container)
        }
        return instance as U
    }
}

class CallbackFactory<T : Any, U : T>(
        private val iface: KClass<T>,
        private val callback: () -> U,
        impl: KClass<out U>,
        instance: U? = null
) : SingletonFactory<U>(impl, instance) {
    override fun getName(): String {
        return iface.qualifiedName as String
    }

    override fun create(container: IContainer, params: Array<*>?): U {
        if (instance == null) {
            instance = callback()
        }
        return instance as U
    }
}
