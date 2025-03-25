package isel.leic.group25.db.entities.timetables

import isel.leic.group25.db.entities.rooms.Room
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import kotlin.time.Duration

@Serializable
sealed interface Session: Entity<Session> {
    companion object: Entity.Factory<Session>()
    val id: Int
    var schoolClass: Class
    var room: Room
    var duration: Duration
}