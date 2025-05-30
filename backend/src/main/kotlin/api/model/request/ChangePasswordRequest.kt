package api.model.request

import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.db.entities.users.User
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
){
    fun validate(): RequestError? {
        val missingFields = listOfNotNull(
            "oldPassword".takeIf { oldPassword.isBlank() },
            "newPassword".takeIf { newPassword.isBlank() }
        )
        if (missingFields.isNotEmpty()) {
            return RequestError.Missing(missingFields)
        }

        if (User.isNotSecurePassword(newPassword)) {
            return RequestError.InsecurePassword
        }

        if (oldPassword == newPassword) {
            return RequestError.Invalid("New password must be different from old password")
        }

        return null
    }
}