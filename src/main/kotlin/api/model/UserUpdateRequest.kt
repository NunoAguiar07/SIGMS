package isel.leic.group25.api.model

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateRequest(
    val username: String,
    val image: ByteArray
)