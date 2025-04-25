package isel.leic.group25.db.tables.users

import isel.leic.group25.db.tables.timetables.Classes
import isel.leic.group25.db.entities.users.Teach
import org.ktorm.schema.Table
import org.ktorm.schema.int


object Teaches : Table<Teach>("teach") {
    val teacherId = int("teacher_id").primaryKey().references(Teachers) { it.teacher }
    val classId = int("class_id").primaryKey().references(Classes) { it.schoolClass }
}