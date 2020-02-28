package rocks.leight.core.job.stats

import rocks.leight.core.api.job.IStatusStats

data class StatusStats(
        override val created: Int,
        override val scheduled: Int,
        override val queued: Int,
        override val running: Int,
        override val successful: Int,
        override val failed: Int
) : IStatusStats
