package isel.leic.group25.api.model.request

import kotlinx.serialization.Serializable

@Serializable
data class RoomRequest(
    val name: String,
    val capacity: Int,
    val type: String,
)