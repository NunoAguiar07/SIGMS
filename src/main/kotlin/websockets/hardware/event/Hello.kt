package isel.leic.group25.websockets.hardware.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("HELLO")
data class Hello(val id: String, val roomId: Int? = null): EventData