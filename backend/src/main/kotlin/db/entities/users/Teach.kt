package isel.leic.group25.db.entities.users

import isel.leic.group25.db.entities.timetables.Class
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface Teach: Entity<Teach> {
    companion object: Entity.Factory<Teach>()
    var teacher: Teacher
    var schoolClass: Class
}