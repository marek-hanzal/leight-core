package rocks.leight.core.job

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.UUIDTable
import org.joda.time.DateTime
import org.joda.time.Interval
import rocks.leight.core.exposed.jsonb
import java.util.*

enum class JobState {
    CREATED,
    SCHEDULED,
    QUEUED,
    WAITING,
    RUNNING,
    SUCCESSFUL,
    FAILED,
}

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

class JobEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<JobEntity>(JobTable)

    var state by JobTable.state
    var schedule by JobTable.schedule
    var error by JobTable.error
    var reset by JobTable.reset
    var message by JobTable.message
    var index by JobTable.index
    var sort by JobTable.sort
    /**
     * when positive, job will be run in future, if negative, it's scheduled in
     * past
     */
    val timeout by lazy {
        try {
            Interval(DateTime(), schedule).toDurationMillis()
        } catch (e: IllegalArgumentException) {
            -Interval(schedule, DateTime()).toDurationMillis()
        }
    }
}
