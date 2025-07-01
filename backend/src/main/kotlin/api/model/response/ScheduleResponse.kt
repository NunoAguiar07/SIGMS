package api.model.response

import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.api.model.response.LectureWithTeacherResponse
import isel.leic.group25.api.model.response.TeacherResponse
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.users.Teacher
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleResponse(
    val lectures: List<LectureWithTeacherResponse>
) {
    companion object {
        fun from(lecturesWithTeachers: List<Pair<Lecture, List<Teacher>>>): ScheduleResponse {
            return ScheduleResponse(
                lectures = lecturesWithTeachers.map { LectureWithTeacherResponse.from(
                    it.first,
                    it.second
                ) }
            )
        }
    }
}