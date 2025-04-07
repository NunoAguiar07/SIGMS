package isel.leic.group25.services.errors

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
}