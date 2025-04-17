package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.issues.IssueReport
import kotlinx.serialization.Serializable

@Serializable
data class IssueReportResponse (
    val id: Int,
    val roomId: Int,
    val description: String,
) {
    companion object {
        fun from(issueReport: IssueReport): IssueReportResponse {
            return IssueReportResponse(
                id = issueReport.id,
                roomId = issueReport.room.id,
                description = issueReport.description
            )
        }
    }
}
