package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class UserClassError {
    data object UserNotFound : UserClassError()
    data object ClassNotFound : UserClassError()
    data object InvalidRole : UserClassError()
    data object InvalidUserClassData : UserClassError()
    data object UserClassChangesFailed : UserClassError()
    data object MissingUserClassData : UserClassError()
    data object InvalidUserId : UserClassError()
    data object InvalidClassId : UserClassError()
    data object FailedToLinkUserToClass : UserClassError()
    data object FailedToUnlinkUserFromClass : UserClassError()
    data object UserNotInClass : UserClassError()
    data object UserAlreadyInClass : UserClassError()

    fun toProblem() : Problem = when (this) {
        UserNotFound -> Problem.notFound(
            title = "User not found",
            detail = "The user with the given ID was not found."
        )
        ClassNotFound -> Problem.notFound(
            title = "Class not found",
            detail = "The class with the given ID was not found."
        )
        InvalidRole -> Problem.badRequest(
            title = "Invalid role",
            detail = "The provided role is invalid."
        )
        InvalidUserClassData -> Problem.badRequest(
            title = "Invalid user class data",
            detail = "The provided user class data is invalid."
        )
        UserClassChangesFailed -> Problem.internalServerError(
            title = "User class changes failed",
            detail = "Failed to update the user class information."
        )
        MissingUserClassData -> Problem.badRequest(
            title = "Missing user class data",
            detail = "The request is missing required user class data."
        )
        InvalidUserId -> Problem.badRequest(
            title = "Invalid user ID",
            detail = "The provided user ID is invalid."
        )
        InvalidClassId -> Problem.badRequest(
            title = "Invalid class ID",
            detail = "The provided class ID is invalid."
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
    }
}