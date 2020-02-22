package rocks.leight.core.api.server

import io.ktor.application.Application

interface IHttpModule {
    fun install(application: Application)
}
