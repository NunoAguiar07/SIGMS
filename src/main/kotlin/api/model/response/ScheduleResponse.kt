package api.model.response

import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.db.entities.timetables.Lecture
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleResponse(
    val lectures: List<LectureResponse>
) {
    companion object {
        fun from(lectures: List<Lecture>): ScheduleResponse {
            return ScheduleResponse(
                lectures = lectures.map { LectureResponse.from(it) }
            )
        }
    }
}