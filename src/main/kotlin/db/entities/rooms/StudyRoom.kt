package isel.leic.group25.db.entities.rooms

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface StudyRoom: Entity<StudyRoom> {
    companion object: Entity.Factory<StudyRoom>()
    val room: Room
}