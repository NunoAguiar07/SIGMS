package isel.leic.group25.api.http.routes

import api.model.request.LoginCredentialsRequest
import api.model.request.UserCredentialsRequest
import api.model.response.LoginResponse
import api.model.response.RegisterResponse
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.oauth2.getMicrosoftOrganizationInfo
import isel.leic.group25.api.oauth2.getMicrosoftUserInfo
import isel.leic.group25.db.entities.types.Role
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
        microsoftLoginRoute(services, client)
        route("/verify-account") {
            accountVerificationRoute(services)
        }
    }
}

fun Route.microsoftLoginRoute(services: Services, client: HttpClient) {
    post("/microsoft") {
        val authHeader = call.request.headers["Authorization"]
            ?: return@post RequestError.Missing("Authorization header").toProblem().respond(call)
        if (!authHeader.startsWith("Bearer ")) {
            return@post RequestError.Invalid("Authorization header format").toProblem().respond(call)
        }
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
        credentials.validate()?.let { error ->
            return@post error.toProblem().respond(call)
        }
        val result = services.from({authService}){
            login(
                email = credentials.email,
                password = credentials.password
            )
        }
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