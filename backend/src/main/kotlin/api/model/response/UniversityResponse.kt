package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.timetables.University
import kotlinx.serialization.Serializable

@Serializable
data class UniversityResponse (
    val id: Int,
    val name: String
) {
    companion object {
        fun fromUniversity(university: University): UniversityResponse {
            return UniversityResponse(
                id = university.id,
                name = university.name
            )
        }
    }
}