package isel.leic.group25.api.exceptions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import isel.leic.group25.api.model.ProblemDetail
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.services.errors.ClassError
import isel.leic.group25.services.errors.RoomError
import isel.leic.group25.services.errors.SubjectError
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

        fun conflict(title: String, detail: String? = null): Problem {
            return Problem(
                typeUri = URI("https://example.com/problems/conflict"),
                title = title,
                status = HttpStatusCode.Conflict,
                detail = detail
            )
        }

        fun notFound(title: String, detail: String? = null): Problem {
            return Problem(
                typeUri = URI("https://example.com/problems/not-found"),
                title = title,
                status = HttpStatusCode.NotFound,
                detail = detail
            )
        }

        fun badRequest(title: String, detail: String? = null): Problem {
            return Problem(
                typeUri = URI("https://example.com/problems/bad-request"),
                title = title,
                status = HttpStatusCode.BadRequest,
                detail = detail
            )
        }

        fun unauthorized(title: String, detail: String? = null): Problem {
            return Problem(
                typeUri = URI("https://example.com/problems/unauthorized"),
                title = title,
                status = HttpStatusCode.Unauthorized,
                detail = detail
            )
        }

        fun internalServerError(title: String, detail: String? = null): Problem {
            return Problem(
                typeUri = URI("https://example.com/problems/internal-server-error"),
                title = title,
                status = HttpStatusCode.InternalServerError,
                detail = detail
            )
        }
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
        AuthError.UserAlreadyExists -> Problem.conflict(
            title = "User already exists",
            detail = "The user with the given username or email already exists."
        )
        AuthError.InsecurePassword -> Problem.badRequest(
            title = "Insecure password",
            detail = "The provided password does not meet the security requirements."
        )
        AuthError.MissingCredentials -> Problem.badRequest(
            title = "Missing credentials",
            detail = "The request is missing required credentials."
        )
        AuthError.UserNotFound -> Problem.notFound(
            title = "User not found",
            detail = "The user with the given username or email was not found."
        )
        AuthError.InvalidCredentials -> Problem.unauthorized(
            title = "Invalid credentials",
            detail = "The provided credentials are invalid."
        )
        AuthError.TokenCreationFailed -> Problem.internalServerError(
            title = "Token creation failed",
            detail = "Failed to create a token for the user."
        )
        AuthError.TokenValidationFailed -> Problem.unauthorized(
            title = "Token validation failed",
            detail = "The provided token is invalid or expired."
        )
        AuthError.UserChangesFailed -> Problem.internalServerError(
            title = "User changes failed",
            detail = "Failed to update the user information."
        )
        AuthError.InvalidRole -> Problem.badRequest(
            title = "Invalid role",
            detail = "The provided role is invalid."
        )
    }
}

fun ClassError.toProblem(): Problem {
    return when (this) {
        ClassError.ClassNotFound -> Problem.notFound(
            title = "Class not found",
            detail = "The class with the given ID was not found."
        )
        ClassError.InvalidRole -> Problem.badRequest(
            title = "Invalid role",
            detail = "The provided role is invalid."
        )
        ClassError.ClassAlreadyExists -> Problem.conflict(
            title = "Class already exists",
            detail = "The class with the given name already exists."
        )
        ClassError.ClassChangesFailed -> Problem.internalServerError(
            title = "Class changes failed",
            detail = "Failed to update the class information."
        )
        ClassError.InvalidClassData -> Problem.badRequest(
            title = "Invalid class data",
            detail = "The provided class data is invalid."
        )
        ClassError.MissingClassData -> Problem.badRequest(
            title = "Missing class data",
            detail = "The request is missing required class data."
        )
        ClassError.SubjectNotFound -> Problem.notFound(
            title = "Subject not found",
            detail = "The subject with the given ID was not found."
        )
        ClassError.InvalidSubjectId -> Problem.badRequest(
            title = "Invalid subject ID",
            detail = "The provided subject ID is invalid."
        )
    }
}

fun SubjectError.toProblem(): Problem {
    return when (this) {
        SubjectError.SubjectNotFound -> Problem.notFound(
            title = "Subject not found",
            detail = "The subject with the given ID was not found."
        )
        SubjectError.InvalidRole -> Problem.badRequest(
            title = "Invalid role",
            detail = "The provided role is invalid."
        )
        SubjectError.SubjectAlreadyExists -> Problem.conflict(
            title = "Subject already exists",
            detail = "The subject with the given name already exists."
        )
        SubjectError.SubjectChangesFailed -> Problem.internalServerError(
            title = "Subject changes failed",
            detail = "Failed to update the subject information."
        )
        SubjectError.InvalidSubjectData -> Problem.badRequest(
            title = "Invalid subject data",
            detail = "The provided subject data is invalid."
        )
        SubjectError.MissingSubjectData -> Problem.badRequest(
            title = "Missing subject data",
            detail = "The request is missing required subject data."
        )
        SubjectError.FailedToAddToDatabase -> Problem.internalServerError(
            title = "Failed to add subject to database",
            detail = "Failed to add the subject to the database."
        )
        SubjectError.InvalidSubjectId -> Problem.badRequest(
            title = "Invalid subject ID",
            detail = "The provided subject ID is invalid."
        )
    }
}

fun RoomError.toProblem(): Problem {
    return when (this) {
        RoomError.RoomNotFound -> Problem.notFound(
            title = "Room not found",
            detail = "The room with the given ID was not found."
        )
        RoomError.RoomAlreadyExists -> Problem.conflict(
            title = "Room already exists",
            detail = "The room with the given name already exists."
        )
        RoomError.InvalidRoomData -> Problem.badRequest(
            title = "Invalid room data",
            detail = "The provided room data is invalid."
        )
        RoomError.InvalidRoomId -> Problem.badRequest(
            title = "Invalid room ID",
            detail = "The provided room ID is invalid."
        )
        RoomError.InvalidRoomCapacity -> Problem.badRequest(
            title = "Invalid room capacity",
            detail = "The provided room capacity is invalid."
        )
    }
}
