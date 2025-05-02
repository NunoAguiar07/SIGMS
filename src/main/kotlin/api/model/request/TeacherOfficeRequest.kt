package isel.leic.group25.api.model.request

import isel.leic.group25.api.exceptions.RequestError
import kotlinx.serialization.Serializable

@Serializable
data class TeacherOfficeRequest(
    val teacherId: Int
) {
    fun validate(): RequestError? {
        if (teacherId <= 0) {
            return RequestError.Invalid("teacherId")
        }
        return null
    }
}