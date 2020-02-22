package rocks.leight.core.api.server

import io.ktor.http.Parameters
import io.ktor.http.ParametersBuilder
import io.ktor.http.Url
import java.net.URLEncoder

interface ILinkGenerator {
    fun href(path: String, parameters: Parameters = Parameters.Empty): Url

    fun href(path: String, parameters: ParametersBuilder.() -> Unit): Url = href(path, Parameters.build { parameters(this) })

    fun encoded(path: String, parameters: Parameters = Parameters.Empty) = href(path.split('/').joinToString("/") { URLEncoder.encode(it, "UTF-8") }, parameters)

    fun encoded(path: String, parameters: ParametersBuilder.() -> Unit): Url = encoded(path, Parameters.build { parameters(this) })
}
