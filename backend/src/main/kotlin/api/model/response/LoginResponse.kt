package api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val accessToken : String,
    val refreshToken: String,
)