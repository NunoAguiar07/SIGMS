package isel.leic.group25.db.tables.timetables

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.tables.rooms.Classrooms
import isel.leic.group25.utils.hoursAndMinutesToDuration
import isel.leic.group25.utils.toHoursAndMinutes
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.time
import org.ktorm.schema.varchar
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Lectures: Table<Lecture>("lecture") {
    val id = int("id").primaryKey().bindTo { it.id }
    val classId = int("class_id").references(Classes){ it.schoolClass }
    val roomId = int("room_id").references(Classrooms){ it.classroom }
    val type = varchar("class_type").transform({ ClassType.valueOf(it.uppercase()) }, {it.name.lowercase()}).bindTo { it.type }
    val weekDay = int("week_day").transform({ WeekDay.fromValueDB(it)},{it.value}).bindTo { it.weekDay }
    val startTime = time("start_time").transform({ "${it.hour}:${it.minute}".hoursAndMinutesToDuration() },{
        val formatter = DateTimeFormatter.ofPattern("H:mm")
        LocalTime.parse(it.toHoursAndMinutes(), formatter) }).bindTo { it.startTime }
    val endTime = time("end_time").transform({ "${it.hour}:${it.minute}".hoursAndMinutesToDuration() },{
        val formatter = DateTimeFormatter.ofPattern("H:mm")
        LocalTime.parse(it.toHoursAndMinutes(), formatter) }).bindTo { it.endTime }
}

