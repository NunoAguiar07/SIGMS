package isel.leic.group25.api.http

import api.model.request.*
import api.model.response.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.exceptions.toProblem
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.model.response.*
import isel.leic.group25.api.model.request.*
import isel.leic.group25.services.*

fun Application.configureRouting(
    userService: UserService,
    classService: ClassService,
    userClassService: UserClassService,
    subjectService: SubjectService,
    roomService: RoomService,
    lectureService: LectureService,
    issuesReportService: IssuesReportService
) {
    routing {
        route("/api"){
            get {
                call.respond(
                    WelcomePageResponse(
                        title = "Welcome to SIGMS",
                        description = "An application to manage classes and schedules",
                        version = "0.0.1"
                    )
                )
            }
            route("/auth") {
                post("/register") {
                    val credentials = call.receive<UserCredentialsRequest>()
                    val result = userService.register(
                        email = credentials.email,
                        username = credentials.username,
                        password = credentials.password,
                        role = credentials.role
                    )
                    call.respondEither(
                        either = result,
                        transformError = { error -> error.toProblem() },
                        transformSuccess = { token ->
                            RegisterResponse(
                                token = token,
                            )
                        },
                        successStatus = HttpStatusCode.Created
                    )
                }
                post("/login") {
                    val credentials = call.receive<LoginCredentialsRequest>()
                    val result = userService.login(
                        email = credentials.email,
                        password = credentials.password
                    )
                    call.respondEither(
                        either = result,
                        transformError = { error -> error.toProblem() },
                        transformSuccess = { token ->
                            LoginResponse(
                                token = token
                            )
                        }
                    )
                }
            }
            authenticate("auth-jwt") {
                route("/profile"){
                    get {
                        val userId = call.getUserIdFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                        val result = userService.getUserById(userId)
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { user ->
                                UserResponse.fromUser(user)
                            }
                        )
                    }
                    put {
                        val userId = call.getUserIdFromPrincipal() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                        val updateRequest = call.receive<UserUpdateRequest>()
                        val result = userService.updateUser(
                            id = userId,
                            username = updateRequest.username,
                            image = updateRequest.image
                        )
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { user ->
                                UserResponse.fromUser(user)
                            }
                        )
                    }
                    post("/password") {
                        val userId = call.getUserIdFromPrincipal() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                        val passwordRequest = call.receive<ChangePasswordRequest>()
                        val result = userService.changePassword(
                            userId = userId,
                            oldPassword = passwordRequest.oldPassword,
                            newPassword = passwordRequest.newPassword
                        )
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { user ->
                                UserResponse.fromUser(user)
                            }
                        )
                    }

                }
                route("/subjects") {
                    get {
                        val result = subjectService.getAllSubjects()
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { subjects ->
                                subjects.map { SubjectResponse.fromSubject(it) }
                            }
                        )
                    }
                    post {
                        val subjectRequest = call.receive<SubjectRequest>()
                        val result = subjectService.createSubject(
                            name = subjectRequest.name
                        )
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { subject ->
                                SubjectResponse.fromSubject(subject)
                            },
                            successStatus = HttpStatusCode.Created
                        )
                    }
                    route("/{subjectId}"){
                        get {
                            val id = call.parameters["id"]
                            val result = subjectService.getSubjectById(id)
                            call.respondEither(
                                either = result,
                                transformError = { error -> error.toProblem() },
                                transformSuccess = { subject ->
                                    SubjectResponse.fromSubject(subject)
                                }
                            )
                        }
                        route("/classes") {
                            get {
                                val id = call.parameters["subjectId"]
                                val result = classService.getAllClassesFromSubject(id)
                                call.respondEither(
                                    either = result,
                                    transformError = { error -> error.toProblem() },
                                    transformSuccess = { classes ->
                                        classes.map { schoolClass ->
                                            ClassResponse.fromClass(schoolClass)
                                        }
                                    }
                                )
                            }
                            post {
                                val id = call.parameters["subjectId"]
                                val classRequest = call.receive<ClassRequest>()
                                val result = classService.createClass(
                                    name = classRequest.name,
                                    subjectId = id
                                )
                                call.respondEither(
                                    either = result,
                                    transformError = { error -> error.toProblem() },
                                    transformSuccess = { schoolClass ->
                                        ClassResponse.fromClass(schoolClass)
                                    },
                                    successStatus = HttpStatusCode.Created
                                )
                            }
                        }
                    }
                }
                route("/rooms"){
                    get {
                        val result = roomService.getAllRooms()
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { rooms ->
                                rooms.map { RoomResponse.from(it) }
                            }
                        )
                    }
                    post {
                        val roomRequest = call.receive<RoomRequest>()
                        val result = roomService.createRoom(
                            name = roomRequest.name,
                            capacity = roomRequest.capacity,
                            type = roomRequest.type
                        )
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { room ->
                                RoomResponse.from(room)
                            },
                            successStatus = HttpStatusCode.Created
                        )
                    }
                }
                route("/lectures"){ //
                    get {
                        val result = lectureService.getAllLectures()
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { lectures ->
                                lectures.map { LectureResponse.from(it) }
                            }
                        )
                    }
                    post {
                        val lectureRequest = call.receive<LectureRequest>()
                        val result = lectureService.createLecture(
                            schoolClassId = lectureRequest.schoolClassId,
                            roomId = lectureRequest.roomId,
                            weekDay = lectureRequest.weekDay,
                            type = lectureRequest.type,
                            startTime = lectureRequest.startTime,
                            endTime = lectureRequest.endTime
                        )
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { lecture ->
                                LectureResponse.from(lecture)
                            },
                            successStatus = HttpStatusCode.Created
                        )
                    }
                }
                route("/issues") {
                    get {
                        val result = issuesReportService.getAllIssueReports()
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { issues ->
                                issues.map { IssueReportResponse.from(it) }
                            }
                        )
                    }
                    post {
                        val issueRequest = call.receive<IssueReportRequest>()
                        val result = issuesReportService.createIssueReport(
                            roomId = issueRequest.roomId,
                            description = issueRequest.description
                        )
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { issue ->
                                IssueReportResponse.from(issue)
                            },
                            successStatus = HttpStatusCode.Created
                        )
                    }
                }
            }
        }
    }

}