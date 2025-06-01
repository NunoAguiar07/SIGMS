package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.http.utils.withRole
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.model.request.IssueReportRequest
import isel.leic.group25.api.model.response.IssueReportResponse
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.Services

fun Route.issueReportRoutes(
    services: Services
) {
    route("/issue-reports") {
        get {
            val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.queryParameters["offset"]?.toIntOrNull() ?: 0
            val unassigned = call.request.queryParameters["unassigned"]?.toBoolean() ?: false
            if(offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
            if(limit < 1 || limit > 100) return@get RequestError.Invalid("limit").toProblem().respond(call)
            val result = services.from({issueReportService}){
                getAllIssueReports(limit, offset, unassigned)
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { issues -> issues.map { IssueReportResponse.from(it) } }
            )
        }

        get("/me") {
            val userId = call.getUserIdFromPrincipal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.queryParameters["offset"]?.toIntOrNull() ?: 0
            if(offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
            if(limit < 1 || limit > 100) return@get RequestError.Invalid("limit").toProblem().respond(call)
            val result = services.from({issueReportService}){
                getIssueReportsByUserId(userId, limit, offset)
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { issues -> issues.map { IssueReportResponse.from(it) } }
            )
        }

        route("/{issueId}") {
            issueCrudRoutes(services)
        }
    }
}

fun Route.issueCrudRoutes(services: Services) {
    get {
        val issueId = call.parameters["issueId"]
        if (issueId.isNullOrBlank()) return@get RequestError.Missing("issueId").toProblem().respond(call)
        if (issueId.toIntOrNull() == null) return@get RequestError.Invalid("issueId").toProblem().respond(call)
        val result = services.from({ issueReportService }) {
            getIssueReportById(issueId.toInt())
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { issue -> IssueReportResponse.from(issue) }
        )
    }

    put("/assign") {
        val userId = call.getUserIdFromPrincipal() ?: return@put call.respond(HttpStatusCode.Unauthorized)
        val issueId = call.parameters["issueId"]
        if(issueId.isNullOrBlank()) return@put call.respond(RequestError.Missing("issueId").toProblem())
        if(issueId.toIntOrNull() == null) return@put call.respond(RequestError.Invalid("issueId").toProblem())
        val result = services.from({issueReportService}){
            assignTechnicianToIssueReport(
                technicianId = userId,
                reportId = issueId.toInt()
            )
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { issue -> IssueReportResponse.from(issue) }
        )
    }


    withRole(Role.TECHNICAL_SERVICE) {
        delete("/delete") {
            val issueId = call.parameters["issueId"]
            if (issueId.isNullOrBlank()) return@delete RequestError.Missing("issueId").toProblem().respond(call)
            if (issueId.toIntOrNull() == null) return@delete RequestError.Invalid("issueId").toProblem().respond(call)
            val result = services.from({ issueReportService }) {
                deleteIssueReport(issueId.toInt())
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { HttpStatusCode.NoContent }
            )
        }
        put("/update") {
            val issueId = call.parameters["issueId"]
            if (issueId.isNullOrBlank()) return@put RequestError.Missing("issueId").toProblem().respond(call)
            if (issueId.toIntOrNull() == null) return@put RequestError.Invalid("issueId").toProblem().respond(call)
            val issueRequest = call.receive<IssueReportRequest>()
            issueRequest.validate()?.let { error ->
                return@put error.toProblem().respond(call)
            }
            val result = services.from({ issueReportService }) {
                updateIssueReport(
                    id = issueId.toInt(),
                    description = issueRequest.description
                )
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { issue -> IssueReportResponse.from(issue) }
            )
        }
    }
}