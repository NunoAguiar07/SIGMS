package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class SubjectError {

    data object SubjectNotFound : SubjectError()
    data object UniversityNotFound : SubjectError()
    data object FailedToAddToDatabase : SubjectError()
    data object SubjectAlreadyExists : SubjectError()
    data object SubjectChangesFailed : SubjectError()
    data object TokenCreationFailed : SubjectError()
    data object AlreadyProcessed : SubjectError()
    data object UnauthorizedRole : SubjectError()
    data class ConnectionDbError(val message: String?) : SubjectError()

    fun toProblem(): Problem {
        return when (this) {
            SubjectNotFound -> Problem.notFound(
                title = "Subject not found",
                detail = "The subject with the given ID was not found."
            )
            UniversityNotFound -> Problem.notFound(
                title = "University not found",
                detail = "The university with the given ID was not found."
            )
            SubjectAlreadyExists -> Problem.conflict(
                title = "Subject already exists",
                detail = "The subject with the given name already exists."
            )
            SubjectChangesFailed -> Problem.internalServerError(
                title = "Subject changes failed",
                detail = "Failed to update the subject information."
            )
            FailedToAddToDatabase -> Problem.internalServerError(
                title = "Failed to add subject to database",
                detail = "Failed to add the subject to the database."
            )
            AlreadyProcessed -> Problem.conflict(
                title = "Already processed",
                detail = "The request has already been processed."
            )
            UnauthorizedRole -> Problem.unauthorized(
                title = "Unauthorized role",
                detail = "You don't have permission to perform this action."
            )
            TokenCreationFailed -> Problem.internalServerError(
                title = "Token creation failed",
                detail = "Failed to create the authentication token."
            )
            is ConnectionDbError -> Problem.internalServerError(
                title = "Database Connection Error",
                detail = "Could not establish connection to the database"
            )
        }
    }
}