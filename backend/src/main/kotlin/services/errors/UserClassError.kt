package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class UserClassError {
    data object UserNotFound : UserClassError()
    data object ClassNotFound : UserClassError()
    data object InvalidRole : UserClassError()
    data object FailedToLinkUserToClass : UserClassError()
    data object FailedToUnlinkUserFromClass : UserClassError()
    data object UserNotInClass : UserClassError()
    data object UserAlreadyInClass : UserClassError()
    data object UserAlreadyInSubject : UserClassError()
    data object NoTeachersFound : UserClassError()
    data class ConnectionDbError(val message: String?) : UserClassError()

    fun toProblem() : Problem = when (this) {
        UserNotFound -> Problem.notFound(
            title = "User not found",
            detail = "The user with the given ID was not found."
        )
        NoTeachersFound -> Problem.notFound(
            title = "No teachers found",
            detail = "No teachers were found for the specified university."
        )
        ClassNotFound -> Problem.notFound(
            title = "Class not found",
            detail = "The class with the given ID was not found."
        )
        InvalidRole -> Problem.badRequest(
            title = "Invalid role",
            detail = "The provided role is invalid."
        )
        FailedToLinkUserToClass -> Problem.internalServerError(
            title = "Failed to link user to class",
            detail = "Failed to link the user to the specified class."
        )
        FailedToUnlinkUserFromClass -> Problem.internalServerError(
            title = "Failed to unlink user from class",
            detail = "Failed to unlink the user from the specified class."
        )
        UserNotInClass -> Problem.badRequest(
            title = "User not in class",
            detail = "The user is not enrolled in the specified class."
        )
        UserAlreadyInClass -> Problem.badRequest(
            title = "User already in class",
            detail = "The user is already enrolled in the specified class."
        )
        UserAlreadyInSubject -> Problem.badRequest(
            title = "User already in subject",
            detail = "The user is already enrolled in the specified subject."
        )
        is ConnectionDbError -> Problem.internalServerError(
            title = "Database Connection Error",
            detail = "Could not establish connection to the database"
        )
    }
}