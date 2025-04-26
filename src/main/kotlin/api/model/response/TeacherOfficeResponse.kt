package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.users.Teacher
import kotlinx.serialization.Serializable

@Serializable
data class TeacherOfficeResponse(
    val teacher: Teacher
){
    companion object {
        fun from(teacher:Teacher): TeacherOfficeResponse{
            return TeacherOfficeResponse(
                teacher = teacher
            )
        }
    }
}