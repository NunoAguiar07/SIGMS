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
 * @param authService Service handling role assessment logic
 * @param userService Service handling user profile operations
 * @param teacherRoomService Service handling teacher-room assignments
 * @param classService Service handling class operations
 * @param userClassService Service handling user-class relationships
 * @param subjectService Service handling subject operations
 * @param roomService Service handling room management
 * @param lectureService Service handling lecture scheduling
 * @param issuesReportService Service handling room issue reports
 */
fun Route.authenticatedRoutes(
    authService: AuthService,
    userService: UserService,
    teacherRoomService: TeacherRoomService,
    classService: ClassService,
    userClassService: UserClassService,
    subjectService: SubjectService,
    roomService: RoomService,
    lectureService: LectureService,
    issuesReportService: IssuesReportService
) {
    authenticate("auth-jwt") {
        assessRoleRoutes(authService)
        profileRoutes(userService)
        subjectRoutes(subjectService, classService, userClassService, lectureService)
        roomRoutes(roomService, lectureService, issuesReportService, teacherRoomService)
        lectureRoutes(lectureService)
        withRoles(setOf(Role.TEACHER, Role.STUDENT)){
            scheduleRoutes(userClassService)
        }
    }
}