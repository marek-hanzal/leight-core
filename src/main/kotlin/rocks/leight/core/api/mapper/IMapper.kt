package rocks.leight.core.api.mapper

interface IMapper<T, U> {
	fun map(item: T): U
}
