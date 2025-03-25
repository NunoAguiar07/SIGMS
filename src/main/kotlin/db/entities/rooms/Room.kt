package isel.leic.group25.db.entities.rooms

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface Room: Entity<Room> {
    companion object: Entity.Factory<Room>()
    val id : Int
    var capacity : Int
}