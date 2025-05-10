package isel.leic.group25.db.tables.rooms

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.tables.timetables.Universities
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object Rooms: Table<Room>("room") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = varchar("room_name").bindTo { it.name }
    val capacity = int("capacity").bindTo { it.capacity }
    val university = int("university_id").references(Universities) { it.university }
}