package isel.leic.group25.api.http.routes

import api.model.request.LoginCredentialsRequest
import api.model.request.UserCredentialsRequest
import api.model.response.LoginResponse
import api.model.response.RegisterResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.AuthService

/**
 * Defines authentication-related routes including registration, login, and account verification.
 *
 * @receiver Route The Ktor route to which these endpoints will be added
 * @param authService The authentication service handling business logic
 */
fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        registerRoute(authService)
        loginRoute(authService)
        route("/verify-account") {
            accountVerificationRoute(authService)
        }
    }
}

/**
 * Handles user registration endpoint.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param authService The authentication service handling registration logic
 */
fun Route.registerRoute(authService: AuthService) {
    post("/register") {
        val credentials = call.receive<UserCredentialsRequest>()
        credentials.validate()?.let { error ->
            return@post error.toProblem().respond(call)
        }
        val result = authService.register(
            email = credentials.email,
            username = credentials.username,
            password = credentials.password,
            role = Role.valueOf(credentials.role.uppercase())
        )
        call.respondEither(
            either = result,
            transformError = { it.toProblem() },
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


/**
 * Handles user login endpoint.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param authService The authentication service handling login logic
 */
fun Route.loginRoute(authService: AuthService) {
    post("/login") {
        val credentials = call.receive<LoginCredentialsRequest>()
        credentials.validate()?.let { error ->
            return@post error.toProblem().respond(call)
        }
        val result = authService.login(
            email = credentials.email,
            password = credentials.password
        )
        call.respondEither(
            either = result,
            transformError = { it.toProblem() },
            transformSuccess = { token ->
                LoginResponse(token = token)
            }
        )
    }
}

/**
 * Handles account verification endpoint for the student using email token.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param authService The authentication service handling verification logic
 */
fun Route.accountVerificationRoute(authService: AuthService) {
    put {
        val token = call.request.queryParameters["token"]
            ?: return@put RequestError.Missing("token").toProblem().respond(call)
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