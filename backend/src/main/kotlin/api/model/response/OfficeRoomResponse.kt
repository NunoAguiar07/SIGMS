package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.rooms.OfficeRoom
import kotlinx.serialization.Serializable

@Serializable
data class OfficeRoomResponse(
    val room: RoomResponse
) {
    companion object {
        fun from(officeRoom: OfficeRoom?): OfficeRoomResponse? =
            officeRoom?.let {
                OfficeRoomResponse(
                    room = RoomResponse.from(it.room)
                )
            }

    }
}