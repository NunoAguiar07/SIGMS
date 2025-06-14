package isel.leic.group25.websockets.hardware.event

import isel.leic.group25.websockets.hardware.enums.Capacity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("RECEIVE_CAPACITY")
data class ReceiveCapacity(val id: String, val capacity: Capacity): EventData