package isel.leic.group25.api.oauth2.domain

import kotlinx.serialization.Serializable

@Serializable
data class MicrosoftUserInfo(
    val mail: String,
    val displayName: String,
)