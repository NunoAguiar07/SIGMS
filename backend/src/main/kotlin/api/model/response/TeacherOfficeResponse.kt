package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.users.Teacher
import kotlinx.serialization.Serializable

@Serializable
data class TeacherOfficeResponse(
    val id: Int,
    val email: String,
    val username: String,
    val officeId: Int,
    val officeRoom: String
){
    companion object {
        fun from(teacher:Teacher): TeacherOfficeResponse{
            return TeacherOfficeResponse(
                teacher.user.id,
                teacher.user.email,
                teacher.user.username,
                teacher.office?.room?.id ?: 0,
                teacher.office?.room?.name ?: ""
            )
        }
    }
}