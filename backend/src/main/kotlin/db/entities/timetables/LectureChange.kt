package isel.leic.group25.db.entities.timetables

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
sealed interface LectureChange : Entity<LectureChange> {
    companion object : Entity.Factory<LectureChange>()
    var lecture: Lecture
    var originalClassroom: Classroom
    var originalWeekDay: WeekDay
    var originalStartTime: Duration
    var originalEndTime: Duration
    var originalType: ClassType
    @OptIn(ExperimentalTime::class)
    var effectiveFrom: Instant?
    @OptIn(ExperimentalTime::class)
    var effectiveUntil: Instant?
    val originalDuration: Duration
        get() = originalEndTime - originalStartTime
}