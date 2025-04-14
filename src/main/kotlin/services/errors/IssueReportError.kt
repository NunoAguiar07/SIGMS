package isel.leic.group25.services.errors


sealed class IssueReportError {
    data object IssueReportNotFound : IssueReportError()
    data object FailedToAddToDatabase : IssueReportError()
    data object FailedToDeleteFromDatabase : IssueReportError()
    data object FailedToUpdateInDatabase : IssueReportError()
    data object InvalidIssueReportId : IssueReportError()
    data object InvalidRoomId : IssueReportError()
    data object InvalidDescription : IssueReportError()
}