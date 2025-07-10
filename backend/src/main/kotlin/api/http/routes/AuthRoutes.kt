package isel.leic.group25.api.http.routes

import api.model.request.LoginCredentialsRequest
import api.model.request.UserCredentialsRequest
import api.model.response.LoginResponse
import api.model.response.RegisterResponse
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.oauth2.getMicrosoftOrganizationInfo
import isel.leic.group25.api.oauth2.getMicrosoftUserInfo
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.LoginResult
import isel.leic.group25.services.Services

/**
 * Defines authentication-related routes including registration, login, and account verification.
 *
 * @receiver Route The Ktor route to which these endpoints will be added
 * @param services The class containing all the services containing business logic
 */
fun Route.authRoutes(services: Services, client: HttpClient) {
    route("/auth") {
        registerRoute(services)
        loginRoute(services)
        refreshTokenRoute(services)
        microsoftLoginRoute(services, client)
        route("/verify-account") {
            accountVerificationRoute(services)
        }
    }
}

private suspend fun loginResponse(call: ApplicationCall, result: LoginResult, device: String){
    call.respondEither(
        either = result,
        transformError = { it.toProblem() },
        transformSuccess = { tokens ->
            if(device == "WEB") {
                val accessCookie = Cookie(
                    name = "auth_token",
                    value = tokens.first,
                    httpOnly = true,
                    maxAge = 60 * 60, // 1 hour
                    secure = true,
                    path = "/"
                )
                val refreshCookie = Cookie(
                    name = "refresh_token",
                    value = tokens.second,
                    httpOnly = true,
                    secure = true,
                    maxAge = 30 * 24 * 60 * 60, // 30 days
                    path = "/"
                )
                call.response.cookies.append(accessCookie)
                call.response.cookies.append(refreshCookie)
                mapOf("success" to true)
            } else {
                LoginResponse(
                    accessToken = tokens.first,
                    refreshToken = tokens.second
                )
            }
        }
    )
}

fun Route.refreshTokenRoute(services: Services) {
    post("/refresh") {
        val device = call.request.headers["X-Device"]
            ?: return@post RequestError.MissingHeader("X-Device").toProblem().respond(call)

        val refreshToken = when (device) {
            "WEB" -> call.request.cookies["refresh_token"]
            else -> call.request.headers["Authorization"]?.removePrefix("Bearer ")
        } ?: return@post RequestError.Missing("refresh token").toProblem().respond(call)

        val result = services.from({ authService }) {
            refreshToken(refreshToken)
        }

        loginResponse(call, result, device)
    }
}

fun Route.microsoftLoginRoute(services: Services, client: HttpClient) {
    post("/microsoft") {
        val authHeader = call.request.headers["Authorization"]
            ?: return@post RequestError.Missing("Authorization header").toProblem().respond(call)
        if (!authHeader.startsWith("Bearer ")) {
            return@post RequestError.Invalid("Authorization header format").toProblem().respond(call)
        }
        val device = call.request.headers["X-Device"]
            ?: return@post RequestError.MissingHeader("X-Device").toProblem().respond(call)
        val accessToken = authHeader.substringAfter("Bearer ")
        val userInfo = getMicrosoftUserInfo(accessToken = accessToken, client = client)
        val organizationInfo = getMicrosoftOrganizationInfo(
            accessToken = accessToken,
            client = client
        )
        if(userInfo == null || organizationInfo == null) {
            return@post RequestError.MicrosoftConnectionFailed.toProblem().respond(call)
        }
        val result = services.from({authService}){
            authenticateWithMicrosoft(userInfo.displayName, userInfo.mail, organizationInfo.displayName)
        }
        loginResponse(call, result, device)
    }
}

/**
 * Handles user registration endpoint.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param authService The authentication service handling registration logic
 */
fun Route.registerRoute(services: Services) {
    post("/register") {
        val credentials = call.receive<UserCredentialsRequest>()
        credentials.validate()?.let { error ->
            return@post error.toProblem().respond(call)
        }
        val result = services.from({authService}){
            register(
                email = credentials.email,
                username = credentials.username,
                password = credentials.password,
                role = Role.valueOf(credentials.role.uppercase()),
                universityId = credentials.universityId
        )}
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
fun Route.loginRoute(services: Services) {
    post("/login") {
        val credentials = call.receive<LoginCredentialsRequest>()
        val device = call.request.headers["X-Device"]
            ?: return@post RequestError.MissingHeader("X-Device").toProblem().respond(call)
        credentials.validate()?.let { error ->
            return@post error.toProblem().respond(call)
        }
        val result = services.from({authService}){
            login(
                email = credentials.email,
                password = credentials.password
            )
        }
        loginResponse(call, result, device)
    }
}

/**
 * Handles account verification endpoint for the student using email token.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param authService The authentication service handling verification logic
 */
fun Route.accountVerificationRoute(services: Services) {
    get {
        val token = call.request.queryParameters["token"]
            ?: return@get RequestError.Missing("token").toProblem().respond(call)
        val result = services.from({authService}){
            verifyStudentAccount(token)
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = {
                HttpStatusCode.NoContent
            }
        )
    }
}