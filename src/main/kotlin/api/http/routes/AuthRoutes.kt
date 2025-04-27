package isel.leic.group25.api.http.routes

import api.model.request.LoginCredentialsRequest
import api.model.request.UserCredentialsRequest
import api.model.response.LoginResponse
import api.model.response.RegisterResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.services.AuthService

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        registerRoute(authService)
        loginRoute(authService)
        route("verifyAccount") {
            accountVerificationRoute(authService)
        }
    }
}

fun Route.registerRoute(authService: AuthService) {
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
            transformSuccess = { check ->
                val message = if(check) {
                    "Check your email to approve your account"
                } else {
                    "Thanks for registering. We will send you an email once your account has been verified"
                }
                RegisterResponse(message)
            },
            successStatus = HttpStatusCode.Created
        )
    }
}

fun Route.loginRoute(authService: AuthService) {
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
                LoginResponse(token = token)
            }
        )
    }
}

fun Route.accountVerificationRoute(authService: AuthService) {
    put("/verifyAccount") {
        val token = call.request.queryParameters["token"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        val result = authService.verifyStudentAccount(token)
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = {
                HttpStatusCode.NoContent
            }
        )
    }
}