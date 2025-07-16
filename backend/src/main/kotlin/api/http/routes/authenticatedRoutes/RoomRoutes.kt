package isel.leic.group25.api.http.routes.authenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.http.utils.withRole
import isel.leic.group25.api.jwt.getUniversityIdFromPrincipal
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
import isel.leic.group25.services.Services

/**
 * Defines all room-related routes including:
 * - Room management (CRUD operations)
 * - Teacher-office assignments
 * - Room lectures
 * - Room issue reporting
 *
 * Routes have role-based access control with different operations available to different roles.
 * @param services The class containing all the services containing business logic
 */
fun Route.roomRoutes(
    services: Services
) {
    route("/rooms") {
        baseRoomRoutes(services)
        withRole(Role.ADMIN){
            teacherOfficeRoutes(services)
        }
        specificRoomRoutes(services)
        roomLecturesRoutes(services)
        roomIssuesRoutes(services)
    }
}

/**
 * Defines basic room operations including:
 * - Listing all rooms (public)
 * - Creating new rooms (admin only)
 */
fun Route.baseRoomRoutes(services: Services) {
    get {
        val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.queryParameters["offset"]?.toIntOrNull() ?: 0
        val searchQuery = call.queryParameters["search"]
        val roomType = call.queryParameters["type"]
        val universityId = call.getUniversityIdFromPrincipal()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)
        if(offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
        if(limit < 1 || limit > 100) return@get RequestError.Invalid("limit").toProblem().respond(call)
        val newRoomType = roomType?.let { RoomType.fromValue(it.uppercase()) }
        if (roomType != null && newRoomType !in listOf(RoomType.CLASS, RoomType.OFFICE, RoomType.STUDY)) {
            return@get RequestError.Invalid("type").toProblem().respond(call)
        }

        val result = services.from({roomService}){
            if(!searchQuery.isNullOrBlank()) {
                getRoomsByNameByTypeAndUniversityId(universityId, searchQuery, newRoomType, limit, offset)
            }
            else if (newRoomType != null) {
                getAllRoomsByUniversityIdAndType(universityId, newRoomType, limit, offset)
            }
            else {
                getAllRooms(limit, offset)
            }
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { rooms -> rooms.map { RoomResponse.from(it) } }
        )
    }
    withRole(Role.ADMIN){
        post("/add") {
            val universityId = call.getUniversityIdFromPrincipal()
                ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val roomRequest = call.receive<RoomRequest>()
            roomRequest.validate()?.let{ error ->
                return@post error.toProblem().respond(call)
            }
            val type = RoomType.fromValue(roomRequest.type.uppercase())
                ?: return@post RequestError.Invalid("type").toProblem().respond(call)
            val result = services.from({roomService}){
                createRoom(
                    name = roomRequest.name,
                    capacity = roomRequest.capacity,
                    type = type,
                    universityId = universityId
                )
            }
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
fun Route.teacherOfficeRoutes(services: Services) {
    route("/offices/{roomId}/teachers") {
        put("/add") {
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) return@put RequestError.Missing("roomId").toProblem().respond(call)
            if(roomId.toIntOrNull() == null) return@put RequestError.Invalid("roomId").toProblem().respond(call)
            val request = call.receive<TeacherOfficeRequest>()
            request.validate()?.let{ error ->
                return@put error.toProblem().respond(call)
            }
            val result = services.from({teacherRoomService}){
                addTeacherToOffice(
                    teacherId = request.teacherId,
                    officeId = roomId.toInt()
                )
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { teacher -> TeacherOfficeResponse.from(teacher) }
            )
        }

        delete("/remove") {
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) return@delete RequestError.Missing("roomId").toProblem().respond(call)
            if(roomId.toIntOrNull() == null) return@delete RequestError.Invalid("roomId").toProblem().respond(call)
            val request = call.receive<TeacherOfficeRequest>()
            val result = services.from({teacherRoomService}){
                removeTeacherFromRoom(
                    teacherId = request.teacherId,
                    officeId = roomId.toInt()
                )
            }
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
fun Route.specificRoomRoutes(services: Services) {
    route("/{roomId}") {
        get {
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) {
                return@get RequestError.Missing("roomId").toProblem().respond(call)
            }
            if(roomId.toIntOrNull() == null) {
                return@get RequestError.Invalid("roomId").toProblem().respond(call)
            }
            val result = services.from({roomService}){
                getRoomById(roomId.toInt())
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { room -> RoomResponse.from(room) }
            )
        }
        withRole(Role.ADMIN){
            delete("/delete") {
                val roomId = call.parameters["roomId"]
                if(roomId.isNullOrBlank()) return@delete RequestError.Missing("roomId").toProblem().respond(call)
                if(roomId.toIntOrNull() == null) return@delete RequestError.Invalid("roomId").toProblem().respond(call)
                val result = services.from({roomService}){
                    deleteRoom(roomId.toInt())
                }
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { HttpStatusCode.NoContent }
                )
            }

            put("update") {
                val roomId = call.parameters["roomId"]
                if(roomId.isNullOrBlank()) return@put RequestError.Missing("roomId").toProblem().respond(call)
                if(roomId.toIntOrNull() == null) return@put RequestError.Invalid("roomId").toProblem().respond(call)
                val roomRequest = call.receive<RoomRequest>()
                roomRequest.validate()?.let{ error ->
                    return@put error.toProblem().respond(call)
                }
                val result = services.from({roomService}){
                    updateRoom(
                        id = roomId.toInt(),
                        name = roomRequest.name,
                        capacity = roomRequest.capacity
                    )
                }
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
fun Route.roomLecturesRoutes(services: Services) {
    route("/{roomId}/lectures") {
        get {
            val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.queryParameters["offset"]?.toIntOrNull() ?: 0
            if(offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
            if(limit < 1 || limit > 100) return@get RequestError.Invalid("limit").toProblem().respond(call)
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) return@get RequestError.Missing("roomId").toProblem().respond(call)
            if(roomId.toIntOrNull() == null) return@get RequestError.Invalid("roomId").toProblem().respond(call)
            val result = services.from({lectureService}){
                getLecturesByRoom(roomId.toInt(), limit, offset)
            }
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
fun Route.roomIssuesRoutes(services: Services) {
    route("/{roomId}/issues") {
        get {
            val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
            val offset = call.queryParameters["offset"]?.toIntOrNull() ?: 0
            if(offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
            if(limit < 1 || limit > 100) return@get RequestError.Invalid("limit").toProblem().respond(call)
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) return@get RequestError.Missing("roomId").toProblem().respond(call)
            if(roomId.toIntOrNull() == null) return@get RequestError.Invalid("roomId").toProblem().respond(call)
            val result = services.from({issueReportService}){
                getIssuesReportByRoomId(roomId.toInt(), limit, offset)
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { issues -> issues.map { IssueReportResponse.from(it) } }
            )
        }

        post("/add") {
            val userId = call.getUserIdFromPrincipal() ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val roomId = call.parameters["roomId"]
            if(roomId.isNullOrBlank()) return@post RequestError.Missing("roomId").toProblem().respond(call)
            if(roomId.toIntOrNull() == null) return@post RequestError.Invalid("roomId").toProblem().respond(call)
            val issueRequest = call.receive<IssueReportRequest>()
            issueRequest.validate()?.let{ error ->
                return@post error.toProblem().respond(call)
            }
            val result = services.from({issueReportService}){
                createIssueReport(
                    userId = userId,
                    roomId = roomId.toInt(),
                    description = issueRequest.description
                )
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { issue -> IssueReportResponse.from(issue) },
                successStatus = HttpStatusCode.Created
            )
        }

    }
}