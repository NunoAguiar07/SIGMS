package isel.leic.group25.api.http.routes.authenticatedRoutes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.jwt.getUniversityIdFromPrincipal
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.jwt.getUserRoleFromPrincipal
import isel.leic.group25.api.model.response.ClassResponse
import isel.leic.group25.api.model.response.UserInfoResponse
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.notifications.ExpoNotifications
import isel.leic.group25.services.Services
import kotlin.text.get

fun Route.userInfoRoutes(services: Services) {
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
    post("/expoToken") {
        val userId = call.getUserIdFromPrincipal() ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val expoToken = call.request.headers["X-Expo-Token"] ?: return@post call.respond(HttpStatusCode.BadRequest)
        ExpoNotifications.saveToken(userId, expoToken)
        call.respond(HttpStatusCode.Created)
    }
    route("/userClasses") {
        get {
            val userId = call.getUserIdFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val role = call.getUserRoleFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val limit = call.queryParameters["limit"] ?.toIntOrNull()?: 10
            if(limit < 1 || limit > 100) return@get RequestError.Invalid("limit").toProblem().respond(call)
            val offset = call.queryParameters["offset"] ?.toIntOrNull() ?: 0
            if(offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
            val result = services.from({userClassService}){
                getUserClasses(userId, Role.valueOf(role.uppercase()), limit, offset)
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { classes ->
                    classes.map { ClassResponse.fromClass(it) }
                }
            )
        }
    }
}