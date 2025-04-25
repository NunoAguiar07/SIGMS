package api.model.response

import kotlinx.serialization.Serializable

@Serializable
sealed class RegisterResponse {
    @Serializable
    data class Success(val token: String) : RegisterResponse()

    @Serializable
    data class PendingApproval(val message: String = "Account pending admin approval") : RegisterResponse()
}