package isel.leic.group25.db.entities.rooms

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface Classroom: Entity<Classroom> {
    companion object: Entity.Factory<Classroom>()
    val room: Room
}