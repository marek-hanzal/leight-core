package rocks.leight.core.job.stats

import rocks.leight.core.api.job.IStats
import rocks.leight.core.api.job.IStatusStats

data class Stats(override val status: IStatusStats) : IStats
