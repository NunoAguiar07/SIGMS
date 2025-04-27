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

fun Route.assessRoleRoutes(authService: AuthService) {
    route("/assessRole") {
        withRole(Role.ADMIN){
            getAllPendingApprovalsRoute(authService)
            processRoleApprovalRoute(authService)
            getApprovalByTokenRoute(authService)
        }
    }
}

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

fun Route.processRoleApprovalRoute(authService: AuthService) {
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