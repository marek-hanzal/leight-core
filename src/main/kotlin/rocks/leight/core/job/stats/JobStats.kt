package rocks.leight.core.job.stats

import rocks.leight.core.api.container.IContainer
import rocks.leight.core.api.job.IJobStats
import rocks.leight.core.api.job.JobException
import rocks.leight.core.api.storage.IStorage

class JobStats(container: IContainer) : IJobStats {
    private val storage: IStorage by container.lazy()

    override fun stats() = Stats(status())

    override fun status() = storage.read {
        exec("""
                SELECT
                    (SELECT COUNT(id) FROM job WHERE state = 'CREATED') AS created,
                    (SELECT COUNT(id) FROM job WHERE state = 'SCHEDULED') AS scheduled,
                    (SELECT COUNT(id) FROM job WHERE state = 'QUEUED') AS queued,
                    (SELECT COUNT(id) FROM job WHERE state = 'RUNNING') AS running,
                    (SELECT COUNT(id) FROM job WHERE state = 'SUCCESSFUL') AS successful,
                    (SELECT COUNT(id) FROM job WHERE state = 'FAILED') AS failed
            """) {
            it.next()
            StatusStats(
                    it.getInt("created"),
                    it.getInt("scheduled"),
                    it.getInt("queued"),
                    it.getInt("running"),
                    it.getInt("successful"),
                    it.getInt("failed")
            )
        } ?: throw JobException("Cannot query job stats!")
    }
}
