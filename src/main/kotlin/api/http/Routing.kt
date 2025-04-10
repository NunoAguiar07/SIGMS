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
import isel.leic.group25.services.ClassService
import isel.leic.group25.services.SubjectService
import isel.leic.group25.services.UserClassService
import isel.leic.group25.services.UserService
import isel.leic.group25.utils.Either

fun Application.configureRouting(
    userService: UserService,
    classService: ClassService,
    userClassService: UserClassService,
    subjectService: SubjectService
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
                route("/schedule"){
                    get {
                        val userId = call.getUserIdFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                        val userRole = userService.getRoleByUserId(userId)
                        call.respondEither(
                            either = userRole,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { role ->
                                when (val result = userClassService.getScheduleByUserId(userId, role.toString())) {
                                    is Either.Right -> {
                                        val schedule = result.value.map { classEntity ->
                                            ClassResponse.fromClass(classEntity)
                                        }
                                        ScheduleResponse(classes = schedule)
                                    }
                                    is Either.Left -> result
                                }
                            }
                        )
                    }
                }
                route("/subject") {
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
                        get ("/class") {
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
                    }
                }
                route("/classes") {
                    post {
                        val classRequest = call.receive<ClassRequest>()
                        val result = classService.createClass(
                            name = classRequest.name,
                            subjectId = classRequest.subjectId,
                            classType = classRequest.classType,
                            startTime = classRequest.startTime,
                            endTime = classRequest.endTime
                        )
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { schoolClasses ->
                                ClassResponse.fromClass(schoolClasses)
                            },
                            successStatus = HttpStatusCode.Created
                        )
                    }
                }
            }
        }
    }

}