package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class ClassError {
    data object ClassNotFound : ClassError()
    data object ClassAlreadyExists : ClassError()
    data object ClassChangesFailed : ClassError()
    data object ClassDeletionFailed : ClassError()
    data object SubjectNotFound : ClassError()
    data class ConnectionDbError(val message: String?) : ClassError()

    fun toProblem(): Problem {
        return when (this) {
            ClassNotFound -> Problem.notFound(
                title = "Class not found",
                detail = "The class with the given ID was not found."
            )
            ClassAlreadyExists -> Problem.conflict(
                title = "Class already exists",
                detail = "The class with the given name already exists."
            )
            ClassChangesFailed -> Problem.internalServerError(
                title = "Class changes failed",
                detail = "Failed to update the class information."
            )
            SubjectNotFound -> Problem.notFound(
                title = "Subject not found",
                detail = "The subject with the given ID was not found."
            )
            ClassDeletionFailed -> Problem.internalServerError(
                title = "Class deletion failed",
                detail = "Failed to delete the class."
            )
            is ConnectionDbError -> Problem.internalServerError(
                title = "Database Connection Error",
                detail = "Could not establish connection to the database"
            )
        }
    }
}