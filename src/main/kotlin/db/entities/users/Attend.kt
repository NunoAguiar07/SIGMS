package isel.leic.group25.db.entities.users

import isel.leic.group25.db.entities.timetables.Class
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface Attend: Entity<Attend> {
    companion object: Entity.Factory<Attend>()
    var user: Student
    var schoolClass: Class
}