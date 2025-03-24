package isel.leic.group25.db.entities.timetables

import isel.leic.group25.db.entities.types.ClassType
import org.ktorm.entity.Entity
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface Class: Entity<Class> {
    val id: Int
    val subject: Subject
    val type: ClassType
    val startTime: Long
    val endTime: Long
    val duration: Duration
        get() = endTime.toDuration(DurationUnit.SECONDS) - startTime.toDuration(DurationUnit.SECONDS)
}