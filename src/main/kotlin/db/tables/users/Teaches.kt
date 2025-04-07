package isel.leic.group25.db.tables.users

import isel.leic.group25.db.tables.timetables.Classes
import isel.leic.group25.db.entities.users.Teach
import org.ktorm.schema.Table
import org.ktorm.schema.int


object Teaches : Table<Teach>("TEACH") {
    val userId = int("user_id").primaryKey().references(Users) { it.user }
    val classId = int("class_id").primaryKey().references(Classes) { it.schoolClass }
}