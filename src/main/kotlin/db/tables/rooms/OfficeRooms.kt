package isel.leic.group25.db.tables.rooms

import isel.leic.group25.db.entities.rooms.OfficeRoom
import org.ktorm.schema.Table
import org.ktorm.schema.int

object OfficeRooms: Table<OfficeRoom>("OFFICE_ROOM") {
    val id = int("id").primaryKey().references(Rooms) { it.room }
}