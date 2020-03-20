package rocks.leight.core.api.rest.discovery

import rocks.leight.core.rest.discovery.Discovery
import rocks.leight.core.rest.discovery.Link
import rocks.leight.core.rest.discovery.Parameter

interface IDiscoveryService {
	fun register(name: String, path: String, description: String, parameters: List<Parameter> = listOf())

	fun register(map: Map<String, Link>)

	fun discovery(): Discovery
}
