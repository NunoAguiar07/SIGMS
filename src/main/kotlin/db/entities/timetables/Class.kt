package isel.leic.group25.db.entities.timetables

import isel.leic.group25.db.entities.types.ClassType
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import kotlin.time.Duration

@Serializable
sealed interface Class: Entity<Class> {
    companion object: Entity.Factory<Class>()
    val id: Int
    var subject: Subject
    var type: ClassType
    var startTime: Instant
    var endTime: Instant
    val duration: Duration
        get() = endTime - startTime
}