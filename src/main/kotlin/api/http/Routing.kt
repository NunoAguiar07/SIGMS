package isel.leic.group25.api.http

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.exceptions.toProblem
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.jwt.getUserRoleFromPrincipal
import isel.leic.group25.api.model.*
import isel.leic.group25.services.ClassService
import isel.leic.group25.services.UserService

fun Application.configureRouting(userService: UserService, classService: ClassService) {
    routing {
        route("/api"){
            get("/") {
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
                    get("/") {
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
                    put("/") {
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
                    get("/") {
                        val userId = call.getUserIdFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                        val role = call.getUserRoleFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                        val result = classService.getScheduleByUserId(userId, role)
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { classes ->
                                val schedule = classes.map { classEntity ->
                                    classEntity.toClassResponse()
                                }
                                ScheduleResponse(
                                    classes = schedule
                                )
                            }
                        )
                    }
                }
            }
        }
    }

}