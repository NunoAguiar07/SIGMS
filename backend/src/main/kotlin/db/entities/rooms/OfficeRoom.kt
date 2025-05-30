package isel.leic.group25.db.entities.rooms

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface OfficeRoom: Entity<OfficeRoom> {
    companion object: Entity.Factory<OfficeRoom>()
    var room: Room
}