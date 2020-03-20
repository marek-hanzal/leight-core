package rocks.leight.core.api.job

interface IJobStats {
	fun stats(): IStats

	fun status(): IStatusStats
}
