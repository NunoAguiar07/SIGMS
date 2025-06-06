package isel.leic.group25.websockets.notifications.event

import kotlinx.serialization.Serializable

@Serializable
data class Acknowledge(val id: String, val userId: String): EventData