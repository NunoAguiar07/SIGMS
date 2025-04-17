package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class AuthError {
    data object UserAlreadyExists : AuthError()
    data object UserChangesFailed : AuthError()
    data object InsecurePassword : AuthError()
    data object MissingCredentials : AuthError()
    data object UserNotFound : AuthError()
    data object InvalidCredentials : AuthError()
    data object TokenCreationFailed : AuthError()
    data object TokenValidationFailed : AuthError()
    data object InvalidRole : AuthError()

    fun toProblem(): Problem {
        return when (this) {
            UserAlreadyExists -> Problem.conflict(
                title = "User already exists",
                detail = "The user with the given username already exists."
            )
            UserChangesFailed -> Problem.internalServerError(
                title = "User changes failed",
                detail = "Failed to update the user information."
            )
            InsecurePassword -> Problem.badRequest(
                title = "Insecure password",
                detail = "The provided password does not meet security requirements."
            )
            MissingCredentials -> Problem.badRequest(
                title = "Missing credentials",
                detail = "The request is missing required authentication credentials."
            )
            UserNotFound -> Problem.notFound(
                title = "User not found",
                detail = "The user with the given ID was not found."
            )
            InvalidCredentials -> Problem.unauthorized(
                title = "Invalid credentials",
                detail = "The provided credentials are invalid."
            )
            TokenCreationFailed -> Problem.internalServerError(
                title = "Token creation failed",
                detail = "Failed to create the authentication token."
            )
            TokenValidationFailed -> Problem.unauthorized(
                title = "Token validation failed",
                detail = "The provided token is invalid or expired."
            )
            InvalidRole -> Problem.badRequest(
                title = "Invalid role",
                detail = "The provided role is invalid."
            )
        }
    }
}