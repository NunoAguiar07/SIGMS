package api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val token : String
)