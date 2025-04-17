package isel.leic.group25.websockets.hardware.event


import kotlinx.serialization.Serializable

@Serializable
class UpdateCapacity: Event {
    override val eventCode: String = "UPDATE_CAPACITY"
}
