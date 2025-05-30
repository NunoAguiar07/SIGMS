package isel.leic.group25.api.model.request

import isel.leic.group25.api.exceptions.RequestError
import kotlinx.serialization.Serializable

@Serializable
data class IssueReportRequest (
    val description: String,
) {
    fun validate(): RequestError?{
        if (description.isBlank()) return RequestError.Invalid("description")
        return null
    }
}
