package isel.leic.group25.db.tables.rooms

import isel.leic.group25.db.entities.rooms.Classroom

import org.ktorm.schema.Table
import org.ktorm.schema.int

object Classrooms: Table<Classroom>("CLASSROOM") {
    val id = int("id").primaryKey().references(Rooms) { it.room }
}