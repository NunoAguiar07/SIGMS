package isel.leic.group25.db.tables.timetables

import isel.leic.group25.db.entities.timetables.LectureChange
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.tables.rooms.Classrooms
import isel.leic.group25.utils.hoursAndMinutesToDuration
import isel.leic.group25.utils.toHoursAndMinutes
import org.ktorm.schema.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

object LectureChanges : Table<LectureChange>("lecture_change") {
    val lectureId = int("lecture_id").references(Lectures) { it.lecture }.primaryKey()
    val originalRoomId = int("original_room_id").references(Classrooms) { it.originalClassroom }
    val originalWeekDay = int("original_week_day").transform(
        { WeekDay.fromValueDB(it) },
        { it.value }
    ).bindTo { it.originalWeekDay }
    val originalStartTime = time("original_start_time").transform(
        { "${it.hour}:${it.minute}".hoursAndMinutesToDuration() },
        {
            val formatter = DateTimeFormatter.ofPattern("H:mm")
            LocalTime.parse(it.toHoursAndMinutes(), formatter)
        }
    ).bindTo { it.originalStartTime }
    val originalEndTime = time("original_end_time").transform(
        { "${it.hour}:${it.minute}".hoursAndMinutesToDuration() },
        {
            val formatter = DateTimeFormatter.ofPattern("H:mm")
            LocalTime.parse(it.toHoursAndMinutes(), formatter)
        }
    ).bindTo { it.originalEndTime }
    val originalType = varchar("original_class_type").transform(
        { ClassType.valueOf(it.uppercase()) },
        {it.name.lowercase()}
    ).bindTo { it.originalType }
    @OptIn(ExperimentalTime::class)
    val effectiveFrom = timestamp("effective_from").transform(
        { it.toKotlinInstant() },
        { it.toJavaInstant() }
    ).bindTo { it.effectiveFrom }
    @OptIn(ExperimentalTime::class)
    val effectiveTo = timestamp("effective_until").transform(
        { it.toKotlinInstant() },
        { it.toJavaInstant() }
    ).bindTo { it.effectiveUntil }
}