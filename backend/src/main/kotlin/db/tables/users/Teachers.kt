package isel.leic.group25.db.tables.users


import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.tables.rooms.OfficeRooms
import org.ktorm.schema.Table
import org.ktorm.schema.int

object Teachers : Table<Teacher>("teacher") {
    val user = int("user_id").primaryKey().references(Users) { it.user }
    val office = int("office_id").references(OfficeRooms) {it.office}
}