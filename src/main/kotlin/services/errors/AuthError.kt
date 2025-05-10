package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class AuthError {
    data object UserAlreadyExists : AuthError()
    data object UserChangesFailed : AuthError()
    data object UserNotFound : AuthError()
    data object UniversityNotFound : AuthError()
    data object UserDeleteFailed : AuthError()
    data object RoleApprovedFailed : AuthError()
    data object AlreadyProcessed : AuthError()
    data object InsecurePassword : AuthError()
    data object TokenValidationFailed : AuthError()
    data object UnauthorizedRole : AuthError()
    data object InvalidCredentials: AuthError()
    data class ConnectionDbError(val message: String?) : AuthError()
    data class EmailError(val message: String?) : AuthError()

    fun toProblem(): Problem {
        return when (this) {
            UserAlreadyExists -> Problem.conflict(
                title = "User already exists",
                detail = "The user with the given email already exists."
            )
            UserChangesFailed -> Problem.internalServerError(
                title = "User changes failed",
                detail = "Failed to update the user information."
            )
            UserNotFound -> Problem.notFound(
                title = "User not found",
                detail = "The user with the given ID was not found."
            )
            UniversityNotFound -> Problem.notFound(
                title = "University not found",
                detail = "The university with the given ID was not found."
            )
            UserDeleteFailed -> Problem.internalServerError(
                title = "User delete failed",
                detail = "Failed to delete the user."
            )
            InsecurePassword -> Problem.badRequest(
                title = "Insecure password",
                detail = "The provided password does not meet security requirements."
            )
            TokenValidationFailed -> Problem.unauthorized(
                title = "Token validation failed",
                detail = "The provided token is invalid or expired."
            )
            RoleApprovedFailed -> Problem.internalServerError(
                title = "Role approval failed",
                detail = "Failed to approve the role."
            )
            AlreadyProcessed -> Problem.conflict(
                title = "Already processed",
                detail = "The request has already been processed."
            )
            UnauthorizedRole -> Problem.unauthorized(
                title = "Unauthorized role",
                detail = "You do not have the required role to perform this action."
            )
            InvalidCredentials -> Problem.badRequest(
                title = "Invalid credentials",
                detail = "Invalid email or password."
            )
            is ConnectionDbError -> Problem.internalServerError(
                title = "Database Connection Error",
                detail = "Could not establish connection to the database"
            )
            is EmailError -> Problem.badRequest(
                title = "Invalid Email",
                detail = "Could not send email properly"
            )
        }
    }
}