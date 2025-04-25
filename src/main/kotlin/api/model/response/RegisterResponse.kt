package api.model.response

import kotlinx.serialization.Serializable

@Serializable
sealed class RegisterResponse {
    @Serializable
    data class PendingCheckStudent(val message: String = "Check your email") : RegisterResponse()

    @Serializable
    data class PendingApproval(val message: String = "Account pending admin approval") : RegisterResponse()
}