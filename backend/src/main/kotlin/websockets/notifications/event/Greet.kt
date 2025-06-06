package isel.leic.group25.websockets.notifications.event

import kotlinx.serialization.Serializable

@Serializable
data class Greet(val userId: String, val platform: String): EventData
