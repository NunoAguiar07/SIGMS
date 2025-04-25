package isel.leic.group25.db.tables.users

import isel.leic.group25.db.entities.users.Attend
import isel.leic.group25.db.tables.timetables.Classes
import org.ktorm.schema.Table
import org.ktorm.schema.int


object Attends: Table<Attend>("attend") {
    val studentId = int("student_id").primaryKey().references(Students) { it.user }
    val classId = int("class_id").primaryKey().references(Classes) { it.schoolClass }
}