package isel.leic.group25.db.tables.users

import isel.leic.group25.db.entities.users.User
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import java.util.*

object Users: Table<User>("\"USER\"") {
    val id = int("id").primaryKey().bindTo { it.id }
    val email = varchar("email").bindTo { it.email }
    var name = varchar("name").bindTo { it.name }
    var password = varchar("password").bindTo { it.password }
    var image = varchar("image")
        .transform({Base64.getDecoder().decode(it)},{Base64.getEncoder().encodeToString(it)})
        .bindTo { it.profileImage }

}