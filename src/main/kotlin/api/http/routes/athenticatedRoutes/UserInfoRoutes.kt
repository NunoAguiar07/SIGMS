package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.jwt.getUniversityIdFromPrincipal
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.jwt.getUserRoleFromPrincipal
import isel.leic.group25.api.model.response.UserInfoResponse
import isel.leic.group25.services.Services
import kotlin.text.get

fun Route.userInfoRoutes() {
    route("/userInfo") {
        get {
            val userId = call.getUserIdFromPrincipal() ?:  return@get call.respond(HttpStatusCode.Unauthorized)
            val universityId = call.getUniversityIdFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val role = call.getUserRoleFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            call.respond(
                UserInfoResponse(
                    userId = userId,
                    universityId = universityId,
                    userRole = role
                )
            )
        }
    }
}