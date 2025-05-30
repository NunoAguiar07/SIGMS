package isel.leic.group25.services.email.model

data class UserDetails(
    val username: String,
    val email: String,
    val requestedRole: String
)