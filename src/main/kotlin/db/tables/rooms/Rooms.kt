package isel.leic.group25.db.tables.rooms

import isel.leic.group25.db.entities.rooms.Room
import org.ktorm.schema.Table
import org.ktorm.schema.int

object Rooms: Table<Room>("ROOM") {
    val id = int("id").primaryKey().bindTo { it.id }
    val capacity = int("capacity").bindTo { it.capacity }
}