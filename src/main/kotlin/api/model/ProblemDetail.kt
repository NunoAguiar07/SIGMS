package isel.leic.group25.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ProblemDetail(
    val type: String,
    val title: String? = null,
    val status: Int? = null,
    val detail: String? = null
)