package isel.leic.group25.api.http.routes.athenticatedRoutes

import api.model.request.ChangePasswordRequest
import api.model.request.UserUpdateRequest
import api.model.response.UserResponse
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.services.UserService

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
fun Route.profileRoutes(userService: UserService) {
    route("/profile") {
        getProfileRoute(userService)
        updateProfileRoute(userService)
        changePasswordRoute(userService)
    }
}

/**
 * Retrieves the current user's profile information.
 * Requires valid authentication.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param userService Service handling user data retrieval
 */
fun Route.getProfileRoute(userService: UserService) {
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
}

/**
 * Updates the current user's profile information.
 * Requires valid authentication.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param userService Service handling profile updates
 */
fun Route.updateProfileRoute(userService: UserService) {
    put("/update") {
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
}

/**
 * Changes the current user's password.
 * Requires valid authentication and current password verification.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param userService Service handling password changes
 */
fun Route.changePasswordRoute(userService: UserService) {
    post("/password") {
        val userId = call.getUserIdFromPrincipal() ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val passwordRequest = call.receive<ChangePasswordRequest>()
        passwordRequest.validate()?.let{ error ->
            return@post call.respond(error.toProblem())
        }
        val result = userService.changePassword(
            userId = userId,
            oldPassword = passwordRequest.oldPassword,
            newPassword = passwordRequest.newPassword
        )
        call.respondEither(
            either = result,
            transformError = { it.toProblem() },
            transformSuccess = { user ->
                UserResponse.fromUser(user)
            }
        )
    }
}

