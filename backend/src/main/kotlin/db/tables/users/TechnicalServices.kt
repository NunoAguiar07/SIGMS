package isel.leic.group25.db.tables.users

import isel.leic.group25.db.entities.users.TechnicalService
import org.ktorm.schema.Table
import org.ktorm.schema.int

object TechnicalServices: Table<TechnicalService>("technical_services") {
    val user = int("user_id").primaryKey().references(Users) { it.user }
}