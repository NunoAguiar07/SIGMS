package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class SubjectError {
    data object SubjectNotFound : SubjectError()
    data object FailedToAddToDatabase : SubjectError()
    data object SubjectAlreadyExists : SubjectError()
    data object InvalidSubjectData : SubjectError()
    data object InvalidSubjectId : SubjectError()
    data object SubjectChangesFailed : SubjectError()
    data object MissingSubjectData : SubjectError()
    data object InvalidRole : SubjectError()
    data object InvalidSubjectLimit : SubjectError()
    data object InvalidSubjectOffset : SubjectError()

    fun toProblem(): Problem {
        return when (this) {
            SubjectNotFound -> Problem.notFound(
                title = "Subject not found",
                detail = "The subject with the given ID was not found."
            )
            InvalidRole -> Problem.badRequest(
                title = "Invalid role",
                detail = "The provided role is invalid."
            )
            SubjectAlreadyExists -> Problem.conflict(
                title = "Subject already exists",
                detail = "The subject with the given name already exists."
            )
            SubjectChangesFailed -> Problem.internalServerError(
                title = "Subject changes failed",
                detail = "Failed to update the subject information."
            )
            InvalidSubjectData -> Problem.badRequest(
                title = "Invalid subject data",
                detail = "The provided subject data is invalid."
            )
            MissingSubjectData -> Problem.badRequest(
                title = "Missing subject data",
                detail = "The request is missing required subject data."
            )
            FailedToAddToDatabase -> Problem.internalServerError(
                title = "Failed to add subject to database",
                detail = "Failed to add the subject to the database."
            )
            InvalidSubjectId -> Problem.badRequest(
                title = "Invalid subject ID",
                detail = "The provided subject ID is invalid."
            )
            InvalidSubjectLimit -> Problem.badRequest(
                title = "Invalid subject limit",
                detail = "The provided subject limit is invalid. It should be between 1 and 100."
            )
            InvalidSubjectOffset -> Problem.badRequest(
                title = "Invalid subject offset",
                detail = "The provided subject offset is invalid. It should be a non-negative integer."
            )
        }
    }
}