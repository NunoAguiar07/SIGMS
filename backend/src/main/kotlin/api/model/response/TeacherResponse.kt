package isel.leic.group25.api.model.response

import api.model.response.UserProfileResponse
import isel.leic.group25.db.entities.users.Teacher
import kotlinx.serialization.Serializable


@Serializable
data class TeacherResponse(
    val user: UserProfileResponse,
    val office: OfficeRoomResponse? = null
) {
    companion object {
        fun from(teacher: Teacher): TeacherResponse {
            return TeacherResponse(
                user = UserProfileResponse.fromUser(teacher.user),
                office = OfficeRoomResponse.from(teacher.office)
            )
        }
    }
}