package isel.leic.group25.api.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginCredentialsRequest(
    val email: String,
    val password: String
)