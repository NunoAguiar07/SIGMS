package isel.leic.group25.websockets.notifications.event

import kotlinx.serialization.Serializable

@Serializable
data class Notification(val id: String, val userId: String, val data: String, val consumed: Boolean = false) : EventData{
    fun toEvent(): Event {
        return Event("notification", this)
    }
}
