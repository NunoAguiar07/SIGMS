package isel.leic.group25

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.http.respondEither
import isel.leic.group25.api.http.toProblem
import isel.leic.group25.api.model.*
import isel.leic.group25.services.AuthService

fun Application.configureRouting(authService: AuthService) {
    routing {
        get("/") {
            call.respond(
                WelcomePageResponse(
                    title = "Welcome to SIGMS",
                    description = "An application to manage classes and schedules",
                    version = "0.0.1",
                    links = listOf(
                        Link("login", "/auth/login"),
                        Link("signup", "/auth/register"),
                    )
                )
            )
        }
        route("/auth") {
            post("/register") {
                val credentials = call.receive<UserCredentialsRequest>()
                val result = authService.register(
                    email = credentials.email,
                    username = credentials.username,
                    password = credentials.password
                )
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { user ->
                        UserResponse(
                            id = user.id,
                            email = user.email,
                            username = user.username,
                            link = listOf(
                                Link("login", "/auth/login"),
                            )
                        )
                    },
                    successStatus = HttpStatusCode.Created
                )
            }
            post("/login") {
                val credentials = call.receive<LoginCredentialsRequest>()
                val result = authService.login(
                    email = credentials.email,
                    password = credentials.password
                )
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { token ->
                        LoginResponse(
                            token = token,
                            links = listOf(
                                Link("home", "/home"),
                            )
                        )
                    }
                )
            }
        }
    }

}