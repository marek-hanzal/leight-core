package rocks.leight.core.job.entity

import org.jetbrains.exposed.dao.UUIDTable
import rocks.leight.core.exposed.jsonb
import rocks.leight.core.job.JobState

/**
 * input job queue; unprocessed raw jobs
 */
object JobTable : UUIDTable("job") {
    val state = enumerationByName("state", 12, JobState::class).default(JobState.CREATED)
    val schedule = datetime("schedule")
    val error = text("error").nullable()
    val reset = bool("reset").default(false)
    val message = jsonb("message")
    val index = long("index").autoIncrement()
    val sort = varchar("sort", 20).index()
}
