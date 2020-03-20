package rocks.leight.core.api.server

interface IHandler {
	suspend fun handle()
}
