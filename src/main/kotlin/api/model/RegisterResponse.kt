package isel.leic.group25.api.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val id: Int,
    val username: String,
    val email: String
)