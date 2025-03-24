package isel.leic.group25.db.tables.users


import isel.leic.group25.db.entities.users.Teacher
import org.ktorm.schema.Table
import org.ktorm.schema.int

object Teachers : Table<Teacher>("TEACHER") {
    val user = int("user_id").primaryKey().references(Users) { it.user }
}