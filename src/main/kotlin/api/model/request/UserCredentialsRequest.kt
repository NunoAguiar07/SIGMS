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
    val role: String
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

        // Check password security (only if password exists)
        if (password.isNotBlank() && User.isNotSecurePassword(password)) {
            return RequestError.InsecurePassword
        }

        // Check role validity (only if role exists)
        if (role.isNotBlank()) {
            val validRoles = Role.entries.joinToString { it.name }
            if (role.uppercase() !in validRoles) {
                return RequestError.Invalid("role")
            }
        }
        return null
    }
}

