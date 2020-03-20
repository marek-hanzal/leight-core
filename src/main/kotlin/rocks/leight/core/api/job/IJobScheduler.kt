package rocks.leight.core.api.job

internal interface IJobScheduler : Runnable {
	fun schedule()
}
