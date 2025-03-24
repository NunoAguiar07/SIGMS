package isel.leic.group25.db.tables.timetables

import isel.leic.group25.db.entities.timetables.Session
import isel.leic.group25.db.tables.rooms.Rooms
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import kotlin.time.Duration

object Sessions: Table<Session>("SESSION") {
    val id = int("id").primaryKey()
    val classId = int("class_id").references(Classes){ it.schoolClass }
    val roomId = int("room_id").references(Rooms){ it.room }
    val duration = varchar("duration").transform({ Duration.parse(it)},{it.toIsoString()}).bindTo { it.duration }
}