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

fun Route.profileRoutes(userService: UserService) {
    route("/profile") {
        getProfileRoute(userService)
        updateProfileRoute(userService)
        changePasswordRoute(userService)
    }
}

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

fun Route.updateProfileRoute(userService: UserService) {
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
}

fun Route.changePasswordRoute(userService: UserService) {
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

