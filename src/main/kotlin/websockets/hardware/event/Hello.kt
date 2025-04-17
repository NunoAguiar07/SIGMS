package isel.leic.group25.websockets.hardware.event

import kotlinx.serialization.Serializable

@Serializable
data class Hello(val id: String, val roomId: String? = null): Event {
    override val eventCode: String = "HELLO_EVENT"
}