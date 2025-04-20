package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import isel.leic.group25.services.*

fun Route.authenticatedRoutes(
    userService: UserService,
    classService: ClassService,
    userClassService: UserClassService,
    subjectService: SubjectService,
    roomService: RoomService,
    lectureService: LectureService,
    issuesReportService: IssuesReportService
) {
    authenticate("auth-jwt") {
        profileRoutes(userService)
        subjectRoutes(subjectService, classService, userClassService, lectureService)
        roomRoutes(roomService, lectureService, issuesReportService)
        lectureRoutes(lectureService)
        scheduleRoutes(userClassService)
    }
}