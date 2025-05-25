package isel.leic.group25.api.http.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.model.response.UniversityResponse
import isel.leic.group25.services.Services

fun Route.universityRoutes(services: Services) {
    get("/universities") {
        val limit = call.queryParameters["limit"] ?.toIntOrNull()?: 10
        if(limit < 1 || limit > 100) return@get call.respond(RequestError.Invalid("limit").toProblem())
        val offset = call.queryParameters["offset"] ?.toIntOrNull() ?: 0
        if(offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
        val searchQuery = call.queryParameters["search"]
        val result = services.from({universityService}) {
            if(searchQuery != null) {
                getUniversitiesByName(limit, offset, searchQuery)
            } else {
                getAllUniversities(limit, offset)
            }
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { universities ->
                universities.map { university ->
                    UniversityResponse.fromUniversity(university)
                }
            }
        )

    }
}