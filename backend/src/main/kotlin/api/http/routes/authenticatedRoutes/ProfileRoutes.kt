package isel.leic.group25.api.http.routes.authenticatedRoutes

import api.model.request.ChangePasswordRequest
import api.model.request.UserUpdateRequest
import api.model.response.UserProfileResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.model.response.TeacherResponse
import isel.leic.group25.services.Services

/**
 * Defines user profile management routes including:
 * - Profile retrieval
 * - Profile updates
 * - Password changes
 *
 * All endpoints require authenticated user access.
 *
 * @receiver Route The Ktor route to which these endpoints will be added
 * @param userService Service handling user profile operations
 */
fun Route.profileRoutes(services: Services) {
    route("/profile") {
        getProfileRoute(services)
        getTeacherProfileByIdRoute(services)
        updateProfileRoute(services)
        changePasswordRoute(services)
    }
}

/**
 * Retrieves the current user's profile information.
 * Requires valid authentication.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param userService Service handling user data retrieval
 */
fun Route.getProfileRoute(services: Services) {
    get {
        val userId = call.getUserIdFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
        val result = services.from({userService}){
            getUserById(userId)
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { user ->
                UserProfileResponse.fromUser(user)
            }
        )
    }
}

/**
 * Retrieves a user's profile by their ID.
 * Requires valid authentication.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param userService Service handling user data retrieval
 */
fun Route.getTeacherProfileByIdRoute(services: Services) {
    get("/{userId}") {
        val userId = call.parameters["userId"]?.toIntOrNull() ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
        val result = services.from({userService}){
            getTeacherById(userId)
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { teacher ->
                TeacherResponse.from(teacher)
            }
        )
    }

}

/**
 * Updates the current user's profile information.
 * Requires valid authentication.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param userService Service handling profile updates
 */
fun Route.updateProfileRoute(services: Services) {
    put("/update") {
        val userId = call.getUserIdFromPrincipal() ?: return@put call.respond(HttpStatusCode.Unauthorized)
        val updateRequest = call.receive<UserUpdateRequest>()
        val result = services.from({userService}){
            updateUser(
                id = userId,
                username = updateRequest.username,
                image = updateRequest.image.map { it.toByte() }.toByteArray()
            )
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { user ->
                UserProfileResponse.fromUser(user)
            }
        )
    }
}

/**
 * Changes the current user's password.
 * Requires valid authentication and current password verification.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param userService Service handling password changes
 */
fun Route.changePasswordRoute(services: Services) {
    post("/password") {
        val userId = call.getUserIdFromPrincipal() ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val passwordRequest = call.receive<ChangePasswordRequest>()
        passwordRequest.validate()?.let{ error ->
            return@post error.toProblem().respond(call)
        }
        val result = services.from({userService}){
            changePassword(
                userId = userId,
                oldPassword = passwordRequest.oldPassword,
                newPassword = passwordRequest.newPassword
            )
        }
        call.respondEither(
            either = result,
            transformError = { it.toProblem() },
            transformSuccess = { user ->
                UserProfileResponse.fromUser(user)
            }
        )
    }
}

