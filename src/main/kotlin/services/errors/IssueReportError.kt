package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem


sealed class IssueReportError {
    data object IssueReportNotFound : IssueReportError()
    data object UserNotFound : IssueReportError()
    data object FailedToAddToDatabase : IssueReportError()
    data object FailedToDeleteFromDatabase : IssueReportError()
    data object FailedToUpdateInDatabase : IssueReportError()
    data object InvalidIssueReportId : IssueReportError()
    data object InvalidRoomId : IssueReportError()
    data object InvalidDescription : IssueReportError()
    data object InvalidIssueReportLimit : IssueReportError()
    data object InvalidIssueReportOffSet : IssueReportError()
    data object InvalidRole : IssueReportError()
    data object InvalidUserId : IssueReportError()

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
            FailedToAddToDatabase -> Problem.internalServerError(
                title = "Failed to add issue report",
                detail = "An error occurred while adding the issue report to the database."
            )
            FailedToDeleteFromDatabase -> Problem.internalServerError(
                title = "Failed to delete issue report",
                detail = "An error occurred while deleting the issue report from the database."
            )
            FailedToUpdateInDatabase -> Problem.internalServerError(
                title = "Failed to update issue report",
                detail = "An error occurred while updating the issue report in the database."
            )
            InvalidIssueReportId -> Problem.badRequest(
                title = "Invalid issue report ID",
                detail = "The provided issue report ID is invalid."
            )
            InvalidRoomId -> Problem.badRequest(
                title = "Invalid room ID",
                detail = "The provided room ID is invalid."
            )
            InvalidDescription -> Problem.badRequest(
                title = "Invalid description",
                detail = "The provided description is invalid or empty."
            )
            InvalidIssueReportLimit -> Problem.badRequest(
                title = "Invalid issue report limit",
                detail = "The provided issue report limit is invalid."
            )
            InvalidIssueReportOffSet -> Problem.badRequest(
                title = "Invalid issue report offset",
                detail = "The provided issue report offset is invalid."
            )
            InvalidRole -> Problem.badRequest (
                title = "Invalid role",
                detail = "The provided role is invalid for this operation."
            )
            InvalidUserId -> Problem.badRequest (
                title = "Invalid user ID",
                detail = "The provided user ID is invalid for this operation."
            )
        }
    }
}