package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.http.utils.withRole
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.model.response.AssessRoleResponse
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.AuthService

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
        val limit = call.queryParameters["limit"]
        val offset = call.queryParameters["offset"]
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
            val status = call.queryParameters["status"]
            val adminId = call.getUserIdFromPrincipal() ?: return@put call.respond(HttpStatusCode.Unauthorized)

            val result = authService.assessRoleRequest(
                token = token,
                adminUserId = adminId,
                status = status
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