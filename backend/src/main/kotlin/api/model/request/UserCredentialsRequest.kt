package api.model.request

import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import kotlinx.serialization.Serializable

@Serializable
data class UserCredentialsRequest(
    val email: String,
    val username: String,
    val password: String,
    val role: String,
    val universityId: Int
) {
    fun validate(): RequestError? {
        // Check for missing fields
        val missingFields = listOfNotNull(
            "email".takeIf { email.isBlank() },
            "username".takeIf { username.isBlank() },
            "password".takeIf { password.isBlank() },
            "role".takeIf { role.isBlank() }
        )
        if (missingFields.isNotEmpty()) {
            return RequestError.Missing(missingFields)
        }

        // Check email validity (only if email exists)
        if (!isValidEmailFast(email)) {
            return RequestError.Invalid("email")
        }

        // Check password security (only if password exists)
        if (password.isNotBlank() && User.isNotSecurePassword(password)) {
            return RequestError.InsecurePassword
        }

        // Check role validity (only if role exists)
        if (Role.fromValue(role.uppercase()) == null) {
            return RequestError.Invalid("role")
        }
        return null
    }

    private fun isValidEmailFast(email: String): Boolean {
        if (email.isBlank()) return false

        // Check for exactly one '@' and at least one '.' after it
        val atIndex = email.indexOf('@')
        if (atIndex <= 0 || atIndex != email.lastIndexOf('@')) return false

        val dotIndex = email.lastIndexOf('.')
        if (dotIndex <= atIndex + 1 || dotIndex == email.length - 1) return false

        return true
    }
}

