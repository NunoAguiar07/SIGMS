package isel.leic.group25.api.http.routes.athenticatedRoutes

import api.model.response.ScheduleResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.jwt.getUserRoleFromPrincipal
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.Services

/**
 * Defines the schedule retrieval route for authenticated users.
 *
 * This endpoint returns the class schedule for the current user based on their role
 * (either as a student or teacher). The schedule includes all classes and lectures
 * associated with the user.
 *
 * @receiver Route The Ktor route to which this endpoint will be added
 * @param services The class containing all the services containing business logic
 */
fun Route.scheduleRoutes(
    services: Services
) {
    route("/schedule") {
        get {
            val id = call.getUserIdFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val role = call.getUserRoleFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val result = services.from({userClassService}){
                getScheduleByUserId(id, Role.valueOf(role.uppercase()))
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { schedules ->
                    ScheduleResponse.from(schedules)
                }
            )
        }
    }
}