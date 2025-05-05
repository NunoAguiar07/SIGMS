package isel.leic.group25.db.tables.timetables

import isel.leic.group25.db.entities.timetables.LectureChange
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.tables.rooms.Classrooms
import isel.leic.group25.utils.hoursAndMinutesToDuration
import isel.leic.group25.utils.toHoursAndMinutes
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.time
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object LectureChanges : Table<LectureChange>("lecture_change") {
    val lectureId = int("lecture_id").references(Lectures) { it.lecture }.primaryKey()
    val newRoomId = int("new_room_id").references(Classrooms) { it.newClassroom }
    val newWeekDay = int("new_week_day").transform(
        { WeekDay.fromValueDB(it) },
        { it.value }
    ).bindTo { it.newWeekDay }
    val newStartTime = time("new_start_time").transform(
        { "${it.hour}:${it.minute}".hoursAndMinutesToDuration() },
        {
            val formatter = DateTimeFormatter.ofPattern("H:mm")
            LocalTime.parse(it.toHoursAndMinutes(), formatter)
        }
    ).bindTo { it.newStartTime }
    val newEndTime = time("new_end_time").transform(
        { "${it.hour}:${it.minute}".hoursAndMinutesToDuration() },
        {
            val formatter = DateTimeFormatter.ofPattern("H:mm")
            LocalTime.parse(it.toHoursAndMinutes(), formatter)
        }
    ).bindTo { it.newEndTime }
    val remainingWeeks = int("remaining_weeks").bindTo { it.remainingWeeks }
}