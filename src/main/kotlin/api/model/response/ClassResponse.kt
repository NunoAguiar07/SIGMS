package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.timetables.Class
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ClassResponse(
    val id: Int,
    val subject: String,
    val type: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String
) {
    companion object {
        fun fromClass(classEntity: Class): ClassResponse {
            val localDateTime = classEntity.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
            return ClassResponse(
                id = classEntity.id,
                subject = classEntity.subject.name,
                type = classEntity.type.name,
                dayOfWeek = localDateTime.dayOfWeek.value,
                startTime = localDateTime.time.toString(),
                endTime = classEntity.duration.toString()
            )
        }
    }
}