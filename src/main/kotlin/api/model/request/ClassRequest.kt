package isel.leic.group25.api.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ClassRequest(
    val name: String,
    val subjectId: Int,
    val classType: String,
    val startTime: String,
    val endTime: String,
    val daysOfWeek: List<String>
)