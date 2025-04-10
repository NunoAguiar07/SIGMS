package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.timetables.Subject
import kotlinx.serialization.Serializable

@Serializable
data class SubjectResponse(
    val id: Int,
    val name: String

) {
    companion object {
        fun fromSubject(subject: Subject): SubjectResponse {
            return SubjectResponse(
                id = subject.id,
                name = subject.name
            )
        }
    }
}
