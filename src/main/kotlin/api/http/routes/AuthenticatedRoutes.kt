package isel.leic.group25.api.http.routes

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import isel.leic.group25.api.http.routes.athenticatedRoutes.*
import isel.leic.group25.api.http.routes.athenticatedRoutes.assessRoleRoutes
import isel.leic.group25.services.*

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
        scheduleRoutes(userClassService)
    }
}