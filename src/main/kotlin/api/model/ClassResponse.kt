package isel.leic.group25.api.model

import isel.leic.group25.db.entities.timetables.Class
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
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
)

fun Class.toClassResponse() : ClassResponse {
    val localDateTime = this.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
    return ClassResponse(
        id = this.id,
        subject = this.subject.name,
        type = this.type.name,
        dayOfWeek = localDateTime.dayOfWeek.value,
        startTime = localDateTime.time.toString(),
        endTime = this.duration.toString()
    )
}