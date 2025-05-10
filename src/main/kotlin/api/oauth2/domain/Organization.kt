package isel.leic.group25.api.oauth2.domain

import kotlinx.serialization.Serializable

@Serializable
data class Organization(
    val id: String,
    val name: String
)