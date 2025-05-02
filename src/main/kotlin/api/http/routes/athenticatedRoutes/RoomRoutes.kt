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
import isel.leic.group25.api.model.request.RoomRequest
import isel.leic.group25.api.model.request.TeacherOfficeRequest
import isel.leic.group25.api.model.response.IssueReportResponse
import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.api.model.response.RoomResponse
import isel.leic.group25.api.model.response.TeacherOfficeResponse
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.RoomType
import isel.leic.group25.services.IssuesReportService
import isel.leic.group25.services.LectureService
import isel.leic.group25.services.RoomService
import isel.leic.group25.services.TeacherRoomService

/**
 * Defines all room-related routes including:
 * - Room management (CRUD operations)
 * - Teacher-office assignments
 * - Room lectures
 * - Room issue reporting
 *
 * Routes have role-based access control with different operations available to different roles.
 *
 * @param roomService Service handling room operations
 * @param lectureService Service handling lecture operations
 * @param issuesReportService Service handling issue reports
 * @param teacherRoomService Service handling teacher-room assignments
 */
fun Route.roomRoutes(
    roomService: RoomService,
    lectureService: LectureService,
    issuesReportService: IssuesReportService,
    teacherRoomService: TeacherRoomService
) {
    route("/rooms") {
        baseRoomRoutes(roomService)
        withRole(Role.ADMIN){
            teacherOfficeRoutes(teacherRoomService)
        }
        specificRoomRoutes(roomService)
        roomLecturesRoutes(lectureService)
        roomIssuesRoutes(issuesReportService)
    }
}

/**
 * Defines basic room operations including:
 * - Listing all rooms (public)
 * - Creating new rooms (admin only)
 */
fun Route.baseRoomRoutes(roomService: RoomService) {
    get {
        val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.queryParameters["offset"]?.toIntOrNull() ?: 0
        if(offset < 0) return@get call.respond(RequestError.Invalid("offset").toProblem())
        if(limit < 1 || limit > 100) return@get call.respond(RequestError.Invalid("limit").toProblem())
        val result = roomService.getAllRooms(limit, offset)
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { rooms -> rooms.map { RoomResponse.from(it) } }
        )
    }
    withRole(Role.ADMIN){
        post("/add") {
            val roomRequest = call.receive<RoomRequest>()
            roomRequest.validate()?.let{ error ->
                return@post call.respond(error.toProblem())
            }
            val type = RoomType.fromValue(roomRequest.type)
                ?: return@post call.respond(RequestError.Invalid("type").toProblem())
            val result = roomService.createRoom(
                name = roomRequest.name,
                capacity = roomRequest.capacity,
                type = type
            )
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { room -> RoomResponse.from(room) },
                successStatus = HttpStatusCode.Created
            )
        }
    }
}

/**
 * Handles teacher-office assignment operations (admin only)
 * - Assign teacher to office
 * - Remove teacher from office
 */
fun Route.teacherOfficeRoutes(teacherRoomService: TeacherRoomService) {
    route("/offices/{roomId}/teachers") {
        put("/add") {
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) return@put call.respond(RequestError.Missing("roomId").toProblem())
            if(roomId.toIntOrNull() == null) return@put call.respond(RequestError.Invalid("roomId").toProblem())
            val request = call.receive<TeacherOfficeRequest>()
            request.validate()?.let{ error ->
                return@put call.respond(error.toProblem())
            }
            val result = teacherRoomService.addTeacherToOffice(
                teacherId = request.teacherId,
                officeId = roomId.toInt()
            )
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { teacher -> TeacherOfficeResponse.from(teacher) }
            )
        }

        delete("/remove") {
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) return@delete call.respond(RequestError.Missing("roomId").toProblem())
            if(roomId.toIntOrNull() == null) return@delete call.respond(RequestError.Invalid("roomId").toProblem())
            val request = call.receive<TeacherOfficeRequest>()
            val result = teacherRoomService.removeTeacherFromRoom(
                teacherId = request.teacherId,
                officeId = roomId.toInt()
            )
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { HttpStatusCode.NoContent }
            )
        }
    }
}

/**
 * Handles operations for specific rooms:
 * - Get room details (public)
 * - Update room (admin only)
 * - Delete room (admin only)
 */
fun Route.specificRoomRoutes(roomService: RoomService) {
    route("/{roomId}") {
        get {
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) return@get call.respond(RequestError.Missing("roomId").toProblem())
            if(roomId.toIntOrNull() == null) return@get call.respond(RequestError.Invalid("roomId").toProblem())
            val result = roomService.getRoomById(roomId.toInt())
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { room -> RoomResponse.from(room) }
            )
        }
        withRole(Role.ADMIN){
            delete("/delete") {
                val roomId = call.parameters["roomId"]
                if(roomId.isNullOrBlank()) return@delete call.respond(RequestError.Missing("roomId").toProblem())
                if(roomId.toIntOrNull() == null) return@delete call.respond(RequestError.Invalid("roomId").toProblem())
                val result = roomService.deleteRoom(roomId.toInt())
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { HttpStatusCode.NoContent }
                )
            }

            put("update") {
                val roomId = call.parameters["roomId"]
                if(roomId.isNullOrBlank()) return@put call.respond(RequestError.Missing("roomId").toProblem())
                if(roomId.toIntOrNull() == null) return@put call.respond(RequestError.Invalid("roomId").toProblem())
                val roomRequest = call.receive<RoomRequest>()
                roomRequest.validate()?.let{ error ->
                    return@put call.respond(error.toProblem())
                }
                val result = roomService.updateRoom(
                    id = roomId.toInt(),
                    name = roomRequest.name,
                    capacity = roomRequest.capacity
                )
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { room -> RoomResponse.from(room) }
                )
            }
        }
    }
}

/**
 * Handles lecture-related operations for specific rooms:
 * - Get all lectures scheduled in a room (public)
 */
fun Route.roomLecturesRoutes(lectureService: LectureService) {
    route("/{roomId}/lectures") {
        get {
            val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.queryParameters["offset"]?.toIntOrNull() ?: 0
            if(offset < 0) return@get call.respond(RequestError.Invalid("offset").toProblem())
            if(limit < 1 || limit > 100) return@get call.respond(RequestError.Invalid("limit").toProblem())
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) return@get call.respond(RequestError.Missing("roomId").toProblem())
            if(roomId.toIntOrNull() == null) return@get call.respond(RequestError.Invalid("roomId").toProblem())
            val result = lectureService.getLecturesByRoom(roomId.toInt(), limit, offset)
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { lectures -> lectures.map { LectureResponse.from(it) } }
            )
        }
    }
}

/**
 * Handles room issue reporting and management:
 * - Get all issues (public)
 * - Create new issue (authenticated users)
 * - Issue CRUD operations (technical service only)
 */
fun Route.roomIssuesRoutes(issuesReportService: IssuesReportService) {
    route("/{roomId}/issues") {
        get {
            val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.queryParameters["offset"]?.toIntOrNull() ?: 0
            if(offset < 0) return@get call.respond(RequestError.Invalid("offset").toProblem())
            if(limit < 1 || limit > 100) return@get call.respond(RequestError.Invalid("limit").toProblem())
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) return@get call.respond(RequestError.Missing("roomId").toProblem())
            if(roomId.toIntOrNull() == null) return@get call.respond(RequestError.Invalid("roomId").toProblem())
            val result = issuesReportService.getIssuesReportByRoomId(roomId.toInt(), limit, offset)
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { issues -> issues.map { IssueReportResponse.from(it) } }
            )
        }

        post("/add") {
            val userId = call.getUserIdFromPrincipal() ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) return@post call.respond(RequestError.Missing("roomId").toProblem())
            if(roomId.toIntOrNull() == null) return@post call.respond(RequestError.Invalid("roomId").toProblem())
            val issueRequest = call.receive<IssueReportRequest>()
            issueRequest.validate()?.let{ error ->
                return@post call.respond(error.toProblem())
            }
            val result = issuesReportService.createIssueReport(
                userId = userId,
                roomId = roomId.toInt(),
                description = issueRequest.description
            )
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { issue -> IssueReportResponse.from(issue) },
                successStatus = HttpStatusCode.Created
            )
        }

        route("/{issueId}") {
            issueCrudRoutes(issuesReportService)
            withRole(Role.TECHNICAL_SERVICE){
                issueAssignmentRoutes(issuesReportService)
            }
        }
    }
}

/**
 * Handles basic issue operations:
 * - Get issue details (public)
 * - Update issue (technical service only)
 * - Delete issue (technical service only)
 */
fun Route.issueCrudRoutes(issuesReportService: IssuesReportService) {
    get {
        val issueId = call.parameters["issueId"]
        if(issueId.isNullOrBlank()) return@get call.respond(RequestError.Missing("issueId").toProblem())
        if(issueId.toIntOrNull() == null) return@get call.respond(RequestError.Invalid("issueId").toProblem())
        val result = issuesReportService.getIssueReportById(issueId.toInt())
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { issue -> IssueReportResponse.from(issue) }
        )
    }

    withRole(Role.TECHNICAL_SERVICE) {
        delete("/delete") {
            val issueId = call.parameters["issueId"]
            if(issueId.isNullOrBlank()) return@delete call.respond(RequestError.Missing("issueId").toProblem())
            if(issueId.toIntOrNull() == null) return@delete call.respond(RequestError.Invalid("issueId").toProblem())
            val result = issuesReportService.deleteIssueReport(issueId.toInt())
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { HttpStatusCode.NoContent }
            )
        }
        put("/update") {
            val issueId = call.parameters["issueId"]
            if(issueId.isNullOrBlank()) return@put call.respond(RequestError.Missing("issueId").toProblem())
            if(issueId.toIntOrNull() == null) return@put call.respond(RequestError.Invalid("issueId").toProblem())
            val issueRequest = call.receive<IssueReportRequest>()
            issueRequest.validate()?.let{ error ->
                return@put call.respond(error.toProblem())
            }
            val result = issuesReportService.updateIssueReport(
                id = issueId.toInt(),
                description = issueRequest.description
            )
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { issue -> IssueReportResponse.from(issue) }
            )
        }
    }
}

/**
 * Handles technician assignment to issues (technical service only)
 */
fun Route.issueAssignmentRoutes(issuesReportService: IssuesReportService) {
    route("/assign") {
        put {
            val userId = call.getUserIdFromPrincipal() ?: return@put call.respond(HttpStatusCode.Unauthorized)
            val issueId = call.parameters["issueId"]
            if(issueId.isNullOrBlank()) return@put call.respond(RequestError.Missing("issueId").toProblem())
            if(issueId.toIntOrNull() == null) return@put call.respond(RequestError.Invalid("issueId").toProblem())
            val result = issuesReportService.assignTechnicianToIssueReport(
                technicianId = userId,
                reportId = issueId.toInt()
            )
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { issue -> IssueReportResponse.from(issue) }
            )
        }
    }
}