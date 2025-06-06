package isel.leic.group25.api.http.routes.authenticatedRoutes

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
import isel.leic.group25.services.Services

/**
 * Defines routes for assessing and managing role approval requests for Technical_Services and Teachers
 * All endpoints under this route require ADMIN role access.
 *
 * @receiver Route The Ktor route to which these endpoints will be added
 * @param authService Service handling role approval logic
 */
fun Route.assessRoleRoutes(services: Services) {
    route("/assess-roles") {
        withRole(Role.ADMIN){
            getAllPendingApprovalsRoute(services)
            processRoleApprovalRoute(services)
            getApprovalByTokenRoute(services)
        }
    }
}

/**
 * Retrieves all pending role approval requests with pagination support.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param authService Service handling approval retrieval logic
 */
fun Route.getAllPendingApprovalsRoute(services: Services) {
    get {
        val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.request.queryParameters["offset"]?.toIntOrNull() ?: 0
        if (offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
        if (limit <= 0 || limit > 100) return@get RequestError.Invalid("limit").toProblem().respond(call)
        val result = services.from({authService}){
            getAllPendingApprovals(limit, offset)
        }
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
fun Route.processRoleApprovalRoute(services: Services) {
    route("/validate"){
        put {
            val token = call.queryParameters["token"]
                ?: return@put RequestError.Missing("token").toProblem().respond(call)
            val status = call.queryParameters["status"]
                ?: return@put RequestError.Missing("status").toProblem().respond(call)
            val adminId = call.getUserIdFromPrincipal() ?: return@put call.respond(HttpStatusCode.Unauthorized)
            val toStatus = Status.fromValue(status.uppercase())
                ?: return@put RequestError.Invalid("status").toProblem().respond(call)
            val result = services.from({authService}){
                assessRoleRequest(
                    token = token,
                    adminUserId = adminId,
                    status = toStatus
                )
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
}

/**
 * Retrieves a specific role approval request by its token.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param authService Service handling approval retrieval logic
 */
fun Route.getApprovalByTokenRoute(services: Services) {
    get("/{token}") {
        val token = call.parameters["token"]
            ?: return@get RequestError.Missing("token").toProblem().respond(call)
        val result = services.from({authService}){
            getApprovalByToken(token)
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { roleApproval ->
                AssessRoleResponse.from(roleApproval)
            }
        )
    }
}