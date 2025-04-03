package isel.leic.group25.api.model

import kotlinx.serialization.Serializable

@Serializable
data class UserCredentials(val email: String,
                val username: String,
                val password: String
)