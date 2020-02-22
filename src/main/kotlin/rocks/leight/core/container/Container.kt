@file:Suppress("UNCHECKED_CAST")

package rocks.leight.core.container

import rocks.leight.core.api.config.Configurator
import rocks.leight.core.api.config.IConfigurable
import rocks.leight.core.api.container.ContainerException
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.container.IFactory
import rocks.leight.core.api.container.LazyDelegate
import kotlin.reflect.KClass

class Container : IContainer {
    private var factories: HashMap<String, IFactory<*>> = hashMapOf()
    private var configurators: HashMap<String, Configurator<*>> = hashMapOf()

    override fun <T : Any> configurator(iface: KClass<T>, configurator: Configurator<T>) {
        configurators[iface.qualifiedName as String] = configurator
    }

    override fun <T : Any> configure(instance: T, configurator: String?) {
        (configurator ?: instance::class.qualifiedName).also {
            if (instance is IConfigurable<*> && configurators.containsKey(it)) {
                (instance as IConfigurable<*>).apply { configurator(configurators[it] as Any?.() -> Unit); configure() }
            }
        }
    }

    override fun <T> register(factory: IFactory<T>) {
        factories[factory.getName()] = factory
    }

    override fun <T : Any, U : T> register(iface: KClass<T>, impl: KClass<U>) = register(InterfaceFactory(iface, impl))

    override fun <T : Any, U : T> register(iface: KClass<T>, callback: IContainer.() -> U) = register(ContainerCallbackFactory(iface, callback, iface))

    override fun <T : Any> register(impl: KClass<T>) = register(SingletonFactory(impl))

    override fun <T : Any> create(iface: String, params: Array<*>?): T {
        if (!factories.containsKey(iface)) {
            register(Class.forName(iface).kotlin)
        }
        return (factories[iface] as IFactory<T>).create(this, params).also { configure(it, iface) }
    }

    override fun <T : Any> create(iface: KClass<T>, params: Array<*>?): T = create(iface.qualifiedName ?: throw ContainerException("Cannot create an instance of unknown class (without qualified name)."))

    override fun <T : Any> lazy(): LazyDelegate<T> = LazyDelegate(this)
}
