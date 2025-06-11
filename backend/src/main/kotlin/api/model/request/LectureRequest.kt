package isel.leic.group25.api.model.request

import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.db.entities.types.ClassType
import kotlinx.serialization.Serializable

@Serializable
data class LectureRequest(
    val id : Int,
    val schoolClassId: Int,
    val roomId: Int,
    val type: String,
    val weekDay: Int,
    val startTime: String,
    val endTime: String
) {
    fun validate(): RequestError?{
        if (id < 0) return RequestError.Invalid("id")
        if (schoolClassId <= 0) return RequestError.Invalid("schoolClassId")
        if (roomId <= 0) return RequestError.Invalid("schoolClassId")
        if (startTime.isBlank()) return RequestError.Missing("startTime")
        if (endTime.isBlank()) return RequestError.Missing("endTime")
        if (startTime > endTime) return RequestError.Invalid("startTime must be before endTime")
        if (type.isBlank()) return RequestError.Missing("type"  )
        if (type.uppercase() !in ClassType.entries.map { it.name }) return RequestError.Invalid("type")
        if (weekDay !in 1..7) return RequestError.Invalid("weekDay")
        return null
    }
}