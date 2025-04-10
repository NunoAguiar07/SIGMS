package api.model.request

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateRequest(
    val username: String,
    val image: ByteArray
)