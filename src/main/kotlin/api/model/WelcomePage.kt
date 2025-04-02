package isel.leic.group25.api.model

import kotlinx.serialization.Serializable

/**
 * Represents the response for the welcome page of the API.
 *
 * @property title The title of the API.
 * @property description A brief description of the API.
 * @property version The version of the API.
 * @property links A list of links related to the API.
 */
@Serializable
data class WelcomePageResponse(
    val title: String,
    val description: String,
    val version: String,
    val links: List<Link>
)