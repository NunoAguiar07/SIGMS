package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.model.request.IssueReportRequest
import isel.leic.group25.api.model.request.RoomRequest
import isel.leic.group25.api.model.response.IssueReportResponse
import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.api.model.response.RoomResponse
import isel.leic.group25.services.IssuesReportService
import isel.leic.group25.services.LectureService
import isel.leic.group25.services.RoomService

fun Route.roomRoutes(
    roomService: RoomService,
    lectureService: LectureService,
    issuesReportService: IssuesReportService
) {
    route("/rooms") {
        get {
            val limit = call.parameters["limit"]
            val offset = call.parameters["offset"]
            val result = roomService.getAllRooms(limit, offset)
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { rooms ->
                    rooms.map { RoomResponse.from(it) }
                }
            )
        }
        post {
            val roomRequest = call.receive<RoomRequest>()
            val result = roomService.createRoom(
                name = roomRequest.name,
                capacity = roomRequest.capacity,
                type = roomRequest.type
            )
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { room ->
                    RoomResponse.from(room)
                },
                successStatus = HttpStatusCode.Created
            )
        }
        route("/{roomId}") {
            get {
                val id = call.parameters["roomId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val result = roomService.getRoomById(id)
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { room ->
                        RoomResponse.from(room)
                    }
                )
            }
            route("/lectures") {
                get {
                    val limit = call.parameters["limit"]
                    val offset = call.parameters["offset"]
                    val id = call.parameters["roomId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val result = lectureService.getLecturesByRoom(id, limit, offset)
                    call.respondEither(
                        either = result,
                        transformError = { error -> error.toProblem() },
                        transformSuccess = { lectures ->
                            lectures.map { LectureResponse.from(it) }
                        }
                    )
                }
            }
            route("/issues") {
                get {
                    val limit = call.parameters["limit"]
                    val offset = call.parameters["offset"]
                    val result = issuesReportService.getAllIssueReports(limit, offset)
                    call.respondEither(
                        either = result,
                        transformError = { error -> error.toProblem() },
                        transformSuccess = { issues ->
                            issues.map { IssueReportResponse.from(it) }
                        }
                    )
                }
                post {
                    val roomId = call.parameters["roomId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val issueRequest = call.receive<IssueReportRequest>()
                    val result = issuesReportService.createIssueReport(
                        roomId = roomId,
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
    }
}