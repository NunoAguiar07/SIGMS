package isel.leic.group25.api.exceptions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import isel.leic.group25.api.model.ProblemDetail
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Either
import java.net.URI

class Problem private constructor(
    val typeUri: URI,
    val title: String,
    val status: HttpStatusCode,
    val detail: String? = null
) {

    // Public property for the type string
    private val type: String = typeUri.toASCIIString()


    companion object {
        //Value representing the type of error that can happen in this case will be json problem as a content-type.
        private val MEDIA_TYPE = ContentType.parse("application/problem+json")

        //Problem details
        val UserAlreadyExists = Problem(
            typeUri = URI("https://example.com/problems/user-already-exists"),
            title = "User already exists",
            status = HttpStatusCode.Conflict,
            detail = "The user already exists in the system"
        )
        val InsecurePassword = Problem(
            typeUri = URI("https://example.com/problems/insecure-password"),
            title = "Insecure password",
            status = HttpStatusCode.UnprocessableEntity,
            detail = "The password provided is insecure"
        )
        val UserOrPasswordAreInvalid = Problem(
            typeUri = URI("https://example.com/problems/user-or-password-are-invalid"),
            title = "User or password are invalid",
            status = HttpStatusCode.Unauthorized,
            detail = "The user or password provided are invalid"
        )
        val UserNotFound = Problem(
            typeUri = URI("https://example.com/problems/user-not-found"),
            title = "User not found",
            status = HttpStatusCode.NotFound,
            detail = "The user was not found in the system"
        )
        val InvalidCredentials = Problem(
            typeUri = URI("https://example.com/problems/invalid-credentials"),
            title = "Invalid credentials",
            status = HttpStatusCode.Unauthorized,
            detail = "The credentials provided are invalid"
        )
        val MissingCredentials = Problem(
            typeUri = URI("https://example.com/problems/missing-credentials"),
            title = "Missing credentials",
            status = HttpStatusCode.BadRequest,
            detail = "The credentials provided are missing"
        )
        val InvalidToken = Problem(
            typeUri = URI("https://example.com/problems/invalid-token"),
            title = "Invalid token",
            status = HttpStatusCode.Unauthorized,
            detail = "The token provided is invalid"
        )
        val ExpiredToken = Problem(
            typeUri = URI("https://example.com/problems/expired-token"),
            title = "Expired token",
            status = HttpStatusCode.Unauthorized,
            detail = "The token provided is expired"
        )
        val UserChangesFailed = Problem(
            typeUri = URI("https://example.com/problems/user-changes-failed"),
            title = "User changes failed",
            status = HttpStatusCode.InternalServerError,
            detail = "The user changes failed"
        )
    }

    suspend fun respond(call: ApplicationCall) {
        call.response.header(HttpHeaders.ContentType, MEDIA_TYPE.toString())
        call.respond(
            status,
            ProblemDetail(
                type = type,
                title = title,
                status = status.value,
                detail = detail
            )
        )
    }
}

suspend fun <L, R> ApplicationCall.respondEither(
    either: Either<L, R>,
    transformError: (L) -> Problem,
    transformSuccess: (R) -> Any,
    successStatus: HttpStatusCode = HttpStatusCode.OK
) {
    when (either) {
        is Either.Right -> respond(successStatus, transformSuccess(either.value))
        is Either.Left -> transformError(either.value).respond(this)
    }
}

fun AuthError.toProblem(): Problem {
    return when (this) {
        AuthError.UserAlreadyExists -> Problem.UserAlreadyExists
        AuthError.InsecurePassword -> Problem.InsecurePassword
        AuthError.MissingCredentials -> Problem.MissingCredentials
        AuthError.UserNotFound -> Problem.UserNotFound
        AuthError.InvalidCredentials -> Problem.InvalidCredentials
        AuthError.TokenCreationFailed -> Problem.InvalidToken
        AuthError.TokenValidationFailed -> Problem.ExpiredToken
        AuthError.UserChangesFailed -> Problem.UserChangesFailed
    }
}
