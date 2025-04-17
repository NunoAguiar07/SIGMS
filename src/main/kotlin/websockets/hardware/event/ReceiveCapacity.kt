package isel.leic.group25.websockets.hardware.event

import isel.leic.group25.websockets.hardware.enums.Capacity
import kotlinx.serialization.Serializable

@Serializable
data class ReceiveCapacity(val id: String, val capacity: Capacity): Event{
    override val eventCode: String = "GET_CAPACITY"
}