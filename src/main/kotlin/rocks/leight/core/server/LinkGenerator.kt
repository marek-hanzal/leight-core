package rocks.leight.core.server

import io.ktor.http.Parameters
import io.ktor.http.Url
import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.server.ILinkGenerator

class LinkGenerator(container: IContainer) : ILinkGenerator {
    private val httpServerConfig: HttpServerConfig by container.lazy()
    private val host by lazy { Url(httpServerConfig.host) }

    override fun href(path: String, parameters: Parameters): Url {
        return Url(host.protocol, host.host, host.port, path, parameters, "", null, null, false)
    }
}
