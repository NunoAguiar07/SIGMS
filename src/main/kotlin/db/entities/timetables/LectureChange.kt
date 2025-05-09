package isel.leic.group25.db.entities.timetables

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.types.WeekDay
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import kotlin.time.Duration

@Serializable
sealed interface LectureChange : Entity<LectureChange> {
    companion object : Entity.Factory<LectureChange>()
    var lecture: Lecture
    var newClassroom: Classroom
    var newWeekDay: WeekDay
    var newStartTime: Duration
    var newEndTime: Duration
    var remainingWeeks: Int
    val newDuration: Duration
        get() = newEndTime - newStartTime
}