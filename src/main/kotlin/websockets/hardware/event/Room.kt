package isel.leic.group25.websockets.hardware.event

import kotlinx.serialization.Serializable


@Serializable
data class Room(val roomId: Int): Event{
    override val eventCode: String = "SET_ROOM"
}
