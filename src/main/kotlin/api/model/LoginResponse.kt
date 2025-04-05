package isel.leic.group25.api.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val links: List<Link>
)