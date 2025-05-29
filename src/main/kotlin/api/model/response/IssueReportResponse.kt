package isel.leic.group25.api.model.response

import isel.leic.group25.db.entities.issues.IssueReport
import kotlinx.serialization.Serializable

@Serializable
data class IssueReportResponse(
    val id: Int,
    val room: RoomResponse,
    val createdByUserId: Int,
    val assignTo: Int?,
    val description: String,
) {
    companion object {
        fun from(issueReport: IssueReport): IssueReportResponse {
            return IssueReportResponse(
                id = issueReport.id,
                room = RoomResponse.from(issueReport.room),
                createdByUserId = issueReport.createdBy.id,
                assignTo = issueReport.assignedTo?.user?.id,
                description = issueReport.description,
            )
        }
    }
}
