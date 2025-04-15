package isel.leic.group25.db.tables.timetables

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.tables.rooms.Rooms
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import kotlin.time.Duration.Companion.minutes

object Lectures: Table<Lecture>("LECTURE") {
    val classId = int("class_id").references(Classes){ it.schoolClass }
    val roomId = int("room_id").references(Rooms){ it.room }
    val type = varchar("class_type").transform({ ClassType.valueOf(it.uppercase()) }, {it.name.lowercase()}).bindTo { it.type }
    val weekDay = int("week_day").transform({ WeekDay.entries.first {weekDay ->  weekDay.value == it }},{it.value})
    val startTime = int("start_time").transform({ it.minutes },{ it.inWholeMinutes.toInt() }).bindTo { it.startTime }
    val endTime = int("end_time").transform({ it.minutes },{ it.inWholeMinutes.toInt() }).bindTo { it.endTime }
}