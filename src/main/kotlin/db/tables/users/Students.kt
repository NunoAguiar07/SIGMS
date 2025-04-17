package isel.leic.group25.db.tables.users


import isel.leic.group25.db.entities.users.Student
import org.ktorm.schema.Table
import org.ktorm.schema.int

object Students : Table<Student>("student") {
    val user = int("user_id").primaryKey().references(Users) { it.user }
}