package isel.leic.group25.db.tables.rooms

import isel.leic.group25.db.entities.rooms.StudyRoom
import org.ktorm.schema.Table
import org.ktorm.schema.int

object StudyRooms: Table<StudyRoom>("study_room") {
    val id = int("id").primaryKey().references(Rooms) { it.room }
}