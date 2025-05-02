package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.http.utils.withRole
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.model.response.AssessRoleResponse
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.Status
import isel.leic.group25.services.AuthService
import java.util.*

/**
 * Defines routes for assessing and managing role approval requests for Technical_Services and Teachers
 * All endpoints under this route require ADMIN role access.
 *
 * @receiver Route The Ktor route to which these endpoints will be added
 * @param authService Service handling role approval logic
 */
fun Route.assessRoleRoutes(authService: AuthService) {
    route("/assess-roles") {
        withRole(Role.ADMIN){
            getAllPendingApprovalsRoute(authService)
            processRoleApprovalRoute(authService)
            getApprovalByTokenRoute(authService)
        }
    }
}

/**
 * Retrieves all pending role approval requests with pagination support.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param authService Service handling approval retrieval logic
 */
fun Route.getAllPendingApprovalsRoute(authService: AuthService) {
    get {
        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
        if (offset < 0) return@get call.respond(RequestError.Invalid("offset").toProblem())
        if (limit <= 0 || limit > 100) return@get call.respond(RequestError.Invalid("limit").toProblem())
        val result = authService.getAllPendingApprovals(limit, offset)
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { approvals ->
                approvals.map { approval ->
                    AssessRoleResponse.from(approval)
                }
            }
        )
    }
}

/**
 * Processes a role approval request (approve/reject).
 * Requires valid admin credentials from JWT.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param authService Service handling approval processing logic
 */
fun Route.processRoleApprovalRoute(authService: AuthService) {
    route("/validate"){
        put {
            val token = call.queryParameters["token"]
                ?: return@put call.respond(RequestError.Missing("token").toProblem())
            val status = call.queryParameters["status"]
                ?: return@put call.respond(RequestError.Missing("status").toProblem())
            val adminId = call.getUserIdFromPrincipal() ?: return@put call.respond(HttpStatusCode.Unauthorized)
            val toStatus = Status.fromValue(status.uppercase(Locale.getDefault()))
                ?: return@put call.respond(RequestError.Invalid("status").toProblem())
            val result = authService.assessRoleRequest(
                token = token,
                adminUserId = adminId,
                status = toStatus
            )
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = {
                    HttpStatusCode.NoContent
                }
            )
        }
    }
}

/**
 * Retrieves a specific role approval request by its token.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param authService Service handling approval retrieval logic
 */
fun Route.getApprovalByTokenRoute(authService: AuthService) {
    get("/{token}") {
        val token = call.parameters["token"]
            ?: return@get call.respond(RequestError.Missing("token").toProblem())
        val result = authService.getApprovalByToken(token)
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { roleApproval ->
                AssessRoleResponse.from(roleApproval)
            }
        )
    }
}