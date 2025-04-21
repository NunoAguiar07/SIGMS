package isel.leic.group25.api.http.routes

import api.model.request.LoginCredentialsRequest
import api.model.request.UserCredentialsRequest
import api.model.response.LoginResponse
import api.model.response.RegisterResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.services.AuthService

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val credentials = call.receive<UserCredentialsRequest>()
            val result = authService.register(
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
            val result = authService.login(
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
}