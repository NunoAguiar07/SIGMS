package api.model.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginCredentialsRequest(
    val email: String,
    val password: String
)