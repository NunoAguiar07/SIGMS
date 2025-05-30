package isel.leic.group25.db.tables.users

import isel.leic.group25.db.entities.users.Admin
import org.ktorm.schema.Table
import org.ktorm.schema.int

object Admins: Table<Admin>("administrator") {
    val user = int("user_id").primaryKey().references(Users) { it.user }
}