package isel.leic.group25.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)