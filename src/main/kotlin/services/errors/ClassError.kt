package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class ClassError {
    data object ClassNotFound : ClassError()
    data object ClassAlreadyExists : ClassError()
    data object InvalidClassData : ClassError()
    data object ClassChangesFailed : ClassError()
    data object MissingClassData : ClassError()
    data object InvalidRole : ClassError()
    data object SubjectNotFound : ClassError()
    data object InvalidSubjectId : ClassError()
    data object InvalidClassLimit : ClassError()
    data object InvalidClassOffset : ClassError()

    fun toProblem(): Problem {
        return when (this) {
            ClassNotFound -> Problem.notFound(
                title = "Class not found",
                detail = "The class with the given ID was not found."
            )
            InvalidRole -> Problem.badRequest(
                title = "Invalid role",
                detail = "The provided role is invalid."
            )
            ClassAlreadyExists -> Problem.conflict(
                title = "Class already exists",
                detail = "The class with the given name already exists."
            )
            ClassChangesFailed -> Problem.internalServerError(
                title = "Class changes failed",
                detail = "Failed to update the class information."
            )
            InvalidClassData -> Problem.badRequest(
                title = "Invalid class data",
                detail = "The provided class data is invalid."
            )
            MissingClassData -> Problem.badRequest(
                title = "Missing class data",
                detail = "The request is missing required class data."
            )
            SubjectNotFound -> Problem.notFound(
                title = "Subject not found",
                detail = "The subject with the given ID was not found."
            )
            InvalidSubjectId -> Problem.badRequest(
                title = "Invalid subject ID",
                detail = "The provided subject ID is invalid."
            )
            InvalidClassLimit -> Problem.badRequest(
                title = "Invalid class limit",
                detail = "The provided class limit is invalid. It should be between 1 and 100."
            )
            InvalidClassOffset -> Problem.badRequest(
                title = "Invalid class offset",
                detail = "The provided class offset is invalid. It should be a non-negative integer."
            )
        }
    }
}