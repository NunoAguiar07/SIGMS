package isel.leic.group25.api.http.routes

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import isel.leic.group25.api.http.routes.athenticatedRoutes.*
import isel.leic.group25.api.http.routes.athenticatedRoutes.assessRoleRoutes
import isel.leic.group25.api.http.utils.withRoles
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.*

/**
 * Defines all routes that require JWT authentication.
 *
 * These routes are protected by JWT authentication and provide access to:
 * - Role assessment endpoints
 * - User profile management
 * - Subject-related operations
 * - Room management
 * - Lecture scheduling
 * - User schedules (for teachers and students)
 *
 * @receiver Route The Ktor route to which these endpoints will be added
 * @param services The class containing all the services containing business logic
 */
fun Route.authenticatedRoutes(
    services: Services
) {
    authenticate("auth-jwt") {
        assessRoleRoutes(services)
        profileRoutes(services)
        subjectRoutes(services)
        roomRoutes(services)
        lectureRoutes(services)
        withRoles(setOf(Role.TEACHER, Role.STUDENT)){
            scheduleRoutes(services)
        }
    }
}