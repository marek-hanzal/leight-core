package rocks.leight.core.api.container

import rocks.leight.core.api.config.Configurator
import kotlin.reflect.KClass

interface IContainer {
	/**
	 * register config callback
	 */
	fun <T : Any> configurator(iface: KClass<T>, configurator: Configurator<T>)

	/**
	 * configure the given instance if there is any configurator for it
	 */
	fun <T : Any> configure(instance: T, configurator: String? = null)

	/**
	 * register a factory to the container
	 */
	fun <T> register(factory: IFactory<T>)

	/**
	 * register implementation to interface binding
	 */
	fun <T : Any, U : T> register(iface: KClass<T>, impl: KClass<U>)

	fun <T : Any, U : T> register(iface: KClass<T>, callback: IContainer.() -> U)

	/**
	 * register a singleton class (service)
	 */
	fun <T : Any> register(impl: KClass<T>)

	/**
	 * actually creates (returns) an instance; instance type
	 * is based on rules defined in instance's factory
	 */
	fun <T : Any> create(iface: String, params: Array<*>? = null): T

	/**
	 * creates an instance by class name
	 */
	fun <T : Any> create(iface: KClass<T>, params: Array<*>? = null): T

	fun <T : Any> lazy(): LazyDelegate<T>
}
