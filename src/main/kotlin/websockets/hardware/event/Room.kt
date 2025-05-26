package isel.leic.group25.websockets.hardware.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("ROOM")
data class Room(val roomId: Int, val roomCapacity: Int): EventData