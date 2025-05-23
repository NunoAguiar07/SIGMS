package isel.leic.group25.api.exceptions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import isel.leic.group25.api.model.ProblemDetail
import isel.leic.group25.utils.Either
import kotlinx.coroutines.async
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

fun <L, R> ApplicationCall.respondEither(
    either: Either<L, R>,
    transformError: (L) -> Problem,
    transformSuccess: (R) -> Any,
    successStatus: HttpStatusCode = HttpStatusCode.OK
) = async {
    when (either) {
        is Either.Right -> {
            val responseBody = when (val successValue = transformSuccess(either.value)) {
                is List<*> -> mapOf("data" to successValue)
                else -> successValue
            }
            respond(successStatus, responseBody)
        }
        is Either.Left -> transformError(either.value).respond(this@respondEither)
    }
}