package isel.leic.group25.api.http.routes.athenticatedRoutes

import api.model.response.ScheduleResponse
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.jwt.getUserRoleFromPrincipal
import isel.leic.group25.services.UserClassService

fun Route.scheduleRoutes(
    userClassService: UserClassService
) {
    route("/schedule") {
        get {
            val id = call.getUserIdFromPrincipal()
            val role = call.getUserRoleFromPrincipal()
            val result = userClassService.getScheduleByUserId(id, role)
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