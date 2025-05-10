package isel.leic.group25.db.tables.users

import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.tables.timetables.Universities
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import java.util.*

object Users: Table<User>("users") {
    val id = int("id").primaryKey().bindTo { it.id }
    val email = varchar("email").bindTo { it.email }
    val username = varchar("username").bindTo { it.username }
    var password = varchar("password").bindTo { it.password }
    var image = varchar("profile_image")
        .transform({Base64.getDecoder().decode(it)},{Base64.getEncoder().encodeToString(it)})
        .bindTo { it.profileImage }
    val university = int("university_id").references(Universities) { it.university }
}