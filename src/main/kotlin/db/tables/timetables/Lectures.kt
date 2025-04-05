package isel.leic.group25.db.tables.timetables

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.tables.rooms.Rooms
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import kotlin.time.Duration

object Lectures: Table<Lecture>("LECTURE") {
    val id = int("id").primaryKey().bindTo { it.id }
    val classId = int("class_id").references(Classes){ it.schoolClass }
    val roomId = int("room_id").references(Rooms){ it.room }
    val duration = varchar("duration").transform({ Duration.parse(it)},{it.toIsoString()}).bindTo { it.duration }
}