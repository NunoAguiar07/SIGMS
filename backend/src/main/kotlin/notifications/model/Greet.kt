package isel.leic.group25.notifications.model

import kotlinx.serialization.Serializable

@Serializable
data class Greet(val userId: Int): EventData
