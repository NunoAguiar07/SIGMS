package isel.leic.group25.db.entities.rooms

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface StudyRoom: Entity<StudyRoom> {
    val room: Room
}