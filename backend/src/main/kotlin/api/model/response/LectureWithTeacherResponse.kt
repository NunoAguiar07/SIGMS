package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.users.Teacher
import kotlinx.serialization.Serializable

@Serializable
data class LectureWithTeacherResponse(
    val lecture: LectureResponse,
    val teacher: List<TeacherResponse>
) {
    companion object {
        fun from(lecture: Lecture, teachers: List<Teacher>): LectureWithTeacherResponse {
            return LectureWithTeacherResponse(
                lecture = LectureResponse.from(lecture),
                teacher = teachers.map { TeacherResponse.from(it) }
            )
        }
    }
}


