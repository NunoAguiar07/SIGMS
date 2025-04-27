package isel.leic.group25.api.http

import io.ktor.server.application.*
import io.ktor.server.routing.*
import isel.leic.group25.api.http.routes.athenticatedRoutes.authenticatedRoutes
import isel.leic.group25.api.http.routes.authRoutes
import isel.leic.group25.api.http.routes.welcomeRoutes
import isel.leic.group25.services.*
import isel.leic.group25.websockets.hardware.route.DeviceRoute

fun Application.configureRouting(
    userService: UserService,
    teacherRoomService: TeacherRoomService,
    authService: AuthService,
    classService: ClassService,
    userClassService: UserClassService,
    subjectService: SubjectService,
    roomService: RoomService,
    lectureService: LectureService,
    issuesReportService: IssuesReportService
) {
    routing {
        route("/api") {
            welcomeRoutes()
            authRoutes(authService)
            authenticatedRoutes(
                authService,
                userService,
                teacherRoomService,
                classService,
                userClassService,
                subjectService,
                roomService,
                lectureService,
                issuesReportService
            )
        }
        with(DeviceRoute){
            install()
        }
    }

}