package rocks.leight.core.pool

data class PoolConfig(
	val url: String,
	val user: String? = null,
	val password: String? = null,
	val size: Int = 5,
	val name: String = "default"
)
