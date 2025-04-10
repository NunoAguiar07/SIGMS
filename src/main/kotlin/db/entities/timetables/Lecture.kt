package isel.leic.group25.db.entities.timetables

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.types.ClassType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import kotlin.time.Duration

@Serializable
sealed interface Lecture: Entity<Lecture> {
    companion object: Entity.Factory<Lecture>()
    var schoolClass: Class
    var room: Room
    var type: ClassType
    var startTime: Instant
    var endTime: Instant
    val duration: Duration
        get() = endTime - startTime
}