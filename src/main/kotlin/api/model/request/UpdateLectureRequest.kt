package isel.leic.group25.api.model.request

import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.db.entities.types.ClassType
import kotlinx.serialization.Serializable

@Serializable
data class UpdateLectureRequest (
    val newSchoolClassId: Int,
    val newRoomId: Int,
    val newType: String,
    val newWeekDay: Int,
    val newStartTime: String,
    val newEndTime: String,
    val numberOfWeeks: Int?
) {
    fun validate(): RequestError?{
        if (newSchoolClassId <= 0) return RequestError.Invalid("newSchoolClassId")
        if (newRoomId <= 0) return RequestError.Invalid("newRoomId")
        if (newStartTime.isBlank()) return RequestError.Missing("newStartTime")
        if (newEndTime.isBlank()) return RequestError.Missing("newEndTime")
        if (newStartTime > newEndTime) return RequestError.Invalid("newStartTime must be before newEndTime")
        if (newType.isBlank()) return RequestError.Missing("newType")
        if (newType.uppercase() !in ClassType.entries.map { it.name }) return RequestError.Invalid("newType")
        if (newWeekDay !in 1..7) return RequestError.Invalid("newWeekDay")
        return null
    }
}
