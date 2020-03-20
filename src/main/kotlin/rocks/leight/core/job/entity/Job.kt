package rocks.leight.core.job.entity

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.joda.time.DateTime
import org.joda.time.Interval
import java.util.*

class Job(id: EntityID<UUID>) : UUIDEntity(id) {
	companion object : UUIDEntityClass<Job>(JobTable)

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
