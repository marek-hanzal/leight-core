package rocks.leight.core.api.server

import rocks.leight.core.api.config.IConfigurable
import kotlin.reflect.KClass

interface IHttpServer : IConfigurable<IHttpServer> {
    fun <TModule : IHttpModule> register(module: KClass<TModule>)

    fun start(name: String? = null)
}
