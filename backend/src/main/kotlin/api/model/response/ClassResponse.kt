package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.timetables.Class
import kotlinx.serialization.Serializable

@Serializable
data class ClassResponse(
    val id: Int,
    val subject: SubjectResponse,
    val name: String,
) {
    companion object {
        fun fromClass(classEntity: Class): ClassResponse {
            return ClassResponse(
                id = classEntity.id,
                subject = SubjectResponse.fromSubject(classEntity.subject),
                name = classEntity.name,
            )
        }
    }
}