package api.model.response

import isel.leic.group25.api.model.response.ClassResponse
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleResponse(
    val classes: List<ClassResponse>
)