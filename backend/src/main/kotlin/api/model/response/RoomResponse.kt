package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.rooms.Room
import kotlinx.serialization.Serializable

@Serializable
data class RoomResponse(
    val id: Int,
    val name: String,
    val capacity: Int
) {
    companion object {
        fun from(room: Room): RoomResponse {
            return RoomResponse(
                id = room.id,
                name = room.name,
                capacity = room.capacity
            )
        }
    }
}