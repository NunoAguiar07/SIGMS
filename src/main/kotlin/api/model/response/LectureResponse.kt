package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay



data class LectureResponse(
    val schoolClass: Int,
    val room: Int,
    val type: ClassType,
    val weekDay: WeekDay,
    val startTime: String,
    val endTime: String,
) {
    companion object {
        fun from(lecture: Lecture): LectureResponse {
            return LectureResponse(
                schoolClass = lecture.schoolClass.id,
                room = lecture.room.id,
                type = lecture.type,
                weekDay = lecture.weekDay,
                startTime = lecture.startTime.toString(),
                endTime = lecture.endTime.toString()
            )
        }
    }
}