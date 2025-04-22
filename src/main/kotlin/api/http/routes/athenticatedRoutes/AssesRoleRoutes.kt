package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.model.response.AssesRoleResponse
import isel.leic.group25.services.AuthService

fun Route.assesRoleRoutes(
    authService: AuthService
) {
    route("/assesRole") {
        get {
            val limit = call.queryParameters["limit"]
            val offset = call.queryParameters["offset"]
            val result = authService.getAllPendingApprovals(limit, offset)
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { approvals ->
                    approvals.map { approval ->
                        AssesRoleResponse.from(approval)
                    }
                }
            )
        }
        put {
            val token = call.queryParameters["token"]
            val status = call.queryParameters["status"]
            val adminId = call.getUserIdFromPrincipal() ?: return@put call.respond(HttpStatusCode.Unauthorized)

            val result = authService.assesRoleRequest(
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
        route("/{token}") {
            get {
                val token = call.parameters["token"]
                val result = authService.getApprovalByToken(token)
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { roleApproval ->
                        AssesRoleResponse.from(roleApproval)
                    }
                )
            }
        }
    }
}