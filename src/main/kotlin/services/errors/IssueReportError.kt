package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem


sealed class IssueReportError {
    data object IssueReportNotFound : IssueReportError()
    data object UserNotFound : IssueReportError()
    data object InvalidIssueReportId : IssueReportError()
    data object InvalidRoomId : IssueReportError()

    fun toProblem(): Problem {
        return when (this) {
            IssueReportNotFound -> Problem.notFound(
                title = "Issue report not found",
                detail = "The issue report with the given ID was not found."
            )
            UserNotFound -> Problem.notFound(
                title = "User not found",
                detail = "The user with the given ID was not found."
            )
            InvalidIssueReportId -> Problem.badRequest(
                title = "Invalid issue report ID",
                detail = "The provided issue report ID is invalid."
            )
            InvalidRoomId -> Problem.badRequest(
                title = "Invalid room ID",
                detail = "The provided room ID is invalid."
            )
        }
    }
}