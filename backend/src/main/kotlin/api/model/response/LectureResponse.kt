package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import kotlinx.serialization.Serializable

@Serializable
data class LectureResponse(
    val id: Int,
    val schoolClass: ClassResponse,
    val room: RoomResponse,
    val type: ClassType,
    val weekDay: WeekDay,
    val startTime: String,
    val endTime: String,
) {
    companion object {
        fun from(lecture: Lecture): LectureResponse {
            return LectureResponse(
                id = lecture.id,
                schoolClass = ClassResponse.fromClass(lecture.schoolClass),
                room = RoomResponse.from(lecture.classroom.room),
                type = lecture.type,
                weekDay = lecture.weekDay,
                startTime = lecture.startTime.toString(),
                endTime = lecture.endTime.toString()
            )
        }
    }
}