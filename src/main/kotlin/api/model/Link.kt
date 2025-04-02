package isel.leic.group25.api.model

import kotlinx.serialization.Serializable

/**
 * Represents a link in the API response.
 *
 * @property rel The relation type of the link.
 * @property href The URL of the link.
 */
@Serializable
data class Link(val rel: String, val href: String)