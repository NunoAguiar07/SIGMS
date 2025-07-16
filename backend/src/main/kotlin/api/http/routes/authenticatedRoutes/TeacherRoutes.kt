package isel.leic.group25.api.http.routes.authenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.jwt.getUniversityIdFromPrincipal
import isel.leic.group25.api.model.response.TeacherResponse

import isel.leic.group25.services.Services

fun Route.teacherRoutes(services: Services) {
    route("/teachers") {
        getAllTeachersRoute(services)
    }
}

fun Route.getAllTeachersRoute(services: Services) {
    get {
        val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.queryParameters["offset"]?.toIntOrNull() ?: 0
        val universityId = call.getUniversityIdFromPrincipal()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)
        if (offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
        if (limit < 1 || limit > 100) return@get RequestError.Invalid("limit").toProblem().respond(call)
        val result = services.from({ userClassService }) {
            getAllTeachersByUniversityId( universityId, limit, offset)
        }
        call.respondEither(
            either = result,
            transformError = { it.toProblem() },
            transformSuccess = { teachers -> teachers.map { TeacherResponse.from(it) } }
        )
    }
}






