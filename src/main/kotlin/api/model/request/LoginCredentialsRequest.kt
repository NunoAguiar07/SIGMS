package api.model.request

import isel.leic.group25.api.exceptions.RequestError
import kotlinx.serialization.Serializable

@Serializable
data class LoginCredentialsRequest(
    val email: String,
    val password: String
) {
    fun validate(): RequestError? {
        val missingFields = mutableListOf<String>().apply {
            if (email.isBlank()) add("email")
            if (password.isBlank()) add("password")
        }
        if (missingFields.isNotEmpty()) {
            return RequestError.Missing(missingFields)
        }

        if (email.isNotBlank() && !email.contains("@")) {
            return RequestError.Invalid("email")
        }
        return null
    }
}