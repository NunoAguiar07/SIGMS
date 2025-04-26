package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.jwt.getUserRoleFromPrincipal
import isel.leic.group25.api.model.request.IssueReportRequest
import isel.leic.group25.api.model.request.RoomRequest
import isel.leic.group25.api.model.request.TeacherOfficeRequest
import isel.leic.group25.api.model.response.IssueReportResponse
import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.api.model.response.RoomResponse
import isel.leic.group25.api.model.response.TeacherOfficeResponse
import isel.leic.group25.services.IssuesReportService
import isel.leic.group25.services.LectureService
import isel.leic.group25.services.RoomService
import isel.leic.group25.services.TeacherRoomService

fun Route.roomRoutes(
    roomService: RoomService,
    lectureService: LectureService,
    issuesReportService: IssuesReportService,
    teacherRoomService: TeacherRoomService
) {
    route("/rooms") {
        get {
            val limit = call.queryParameters["limit"]
            val offset = call.queryParameters["offset"]
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
        route("/offices/{roomId}/teachers") {
            put {
                val id = call.parameters["roomId"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<TeacherOfficeRequest>()
                val result = teacherRoomService.addTeacherToOffice(
                    teacherId = request.teacherId,
                    officeId = id
                )
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { teacher ->
                        TeacherOfficeResponse.from(teacher)
                    }
                )
            }
            delete {
                val id = call.parameters["roomId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<TeacherOfficeRequest>()
                val result = teacherRoomService.removeTeacherFromRoom(
                    teacherId = request.teacherId,
                    officeId = id
                )
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { teacher ->
                        TeacherOfficeResponse.from(teacher)
                    }
                )
            }
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
            delete {
                val id = call.parameters["roomId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val result = roomService.deleteRoom(id)
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = {
                        HttpStatusCode.NoContent
                    }
                )
            }
            put {
                val id = call.parameters["roomId"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                val roomRequest = call.receive<RoomRequest>()
                val result = roomService.updateRoom(
                    id = id,
                    name = roomRequest.name,
                    capacity = roomRequest.capacity
                )
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
                    val limit = call.queryParameters["limit"]
                    val offset = call.queryParameters["offset"]
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
                    val limit = call.queryParameters["limit"]
                    val offset = call.queryParameters["offset"]
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
                    val userId = call.getUserIdFromPrincipal()
                    val roomId = call.parameters["roomId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                    val issueRequest = call.receive<IssueReportRequest>()
                    val result = issuesReportService.createIssueReport(
                        userId = userId,
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
                    delete {
                        val role = call.getUserRoleFromPrincipal()
                        val id = call.parameters["issueId"]
                        val result = issuesReportService.deleteIssueReport(id, role)
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = {
                                HttpStatusCode.NoContent
                            }
                        )
                    }
                    put {
                        val role = call.getUserRoleFromPrincipal()
                        val id = call.parameters["issueId"]
                        val issueRequest = call.receive<IssueReportRequest>()
                        val result = issuesReportService.updateIssueReport(
                            id = id,
                            description = issueRequest.description,
                            role = role
                        )
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { issue ->
                                IssueReportResponse.from(issue)
                            }
                        )
                    }
                    route("/assign") {
                        put {
                            val userId = call.getUserIdFromPrincipal()
                            val issueId = call.parameters["issueId"]
                            val result = issuesReportService.assignTechnicianToIssueReport(
                                technicianId = userId,
                                reportId = issueId
                            )
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
}