package isel.leic.group25.api.model.request

import isel.leic.group25.api.exceptions.RequestError
import kotlinx.serialization.Serializable

@Serializable
data class SubjectRequest(
    val name: String
) {
    fun validate(): RequestError? {
        if (name.isEmpty()) return RequestError.Invalid("name")
        return null
    }
}
