package isel.leic.group25.api.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ClassRequest(
    val name: String
)