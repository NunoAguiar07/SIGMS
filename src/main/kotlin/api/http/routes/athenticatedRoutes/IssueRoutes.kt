package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.exceptions.toProblem
import isel.leic.group25.api.model.request.IssueReportRequest
import isel.leic.group25.api.model.response.IssueReportResponse
import isel.leic.group25.services.IssuesReportService

fun Route.issueRoutes(issuesReportService: IssuesReportService) {
    route("/issues") {
        get {
            val result = issuesReportService.getAllIssueReports()
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { issues ->
                    issues.map { IssueReportResponse.from(it) }
                }
            )
        }
        post {
            val issueRequest = call.receive<IssueReportRequest>()
            val result = issuesReportService.createIssueReport(
                roomId = issueRequest.roomId,
                description = issueRequest.description
            )
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { issue ->
                    IssueReportResponse.from(issue)
                },
                successStatus = HttpStatusCode.Created
            )
        }
        route("{issueId}") {
            get {
                val id = call.parameters["issueId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val result = issuesReportService.getIssueReportById(id)
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { issue ->
                        IssueReportResponse.from(issue)
                    }
                )
            }
        }
    }
}