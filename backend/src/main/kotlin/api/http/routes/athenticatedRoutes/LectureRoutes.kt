package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.http.utils.withRole
import isel.leic.group25.api.http.utils.withRoles
import isel.leic.group25.api.model.request.LectureRequest
import isel.leic.group25.api.model.request.UpdateLectureRequest
import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.services.Services
import kotlin.time.ExperimentalTime

/**
 * Defines lecture management routes including creation, retrieval, updating and deletion.
 * Routes have role-based access control with different operations available to different roles.
 *
 * @receiver Route The Ktor route to which these endpoints will be added
 * @param services The class containing all the services containing business logic
 */
fun Route.lectureRoutes(services: Services) {
    route("/lectures") {
        getAllLecturesRoute(services)
        withRole(Role.ADMIN){
            createLectureRoute(services)
        }
        route("/{lectureId}") {
            getLectureRoute(services)
            withRole(Role.ADMIN){
                deleteLectureRoute(services)
            }
            withRoles(setOf(Role.ADMIN, Role.TEACHER)){
                updateLectureRoute(services)
            }
        }
    }
}

/**
 * Retrieves all lectures with optional pagination parameters.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param services The class containing all the services containing business logic
 */
fun Route.getAllLecturesRoute(services: Services) {
    get {
        val limit = call.queryParameters["limit"]?.toIntOrNull() ?: 10
        val offset = call.queryParameters["offset"]?.toIntOrNull() ?: 0
        if(offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
        if(limit < 1 || limit > 100) return@get RequestError.Invalid("limit").toProblem().respond(call)
        val result = services.from({lectureService}){
            getAllLectures(limit, offset)
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { lectures ->
                lectures.map { LectureResponse.from(it) }
            }
        )
    }
}

/**
 * Retrieves a lecture by ID.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param services The class containing all the services containing business logic
 **/
fun Route.getLectureRoute(services: Services) {
    get {
        val lectureStringId = call.parameters["lectureId"]
            ?: return@get RequestError.Missing("lectureId").toProblem().respond(call)
        val lectureId = lectureStringId.toIntOrNull()
            ?: return@get RequestError.Invalid("lectureId").toProblem().respond(call)
        val result = services.from({lectureService}){
            getLectureById(lectureId)
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { lecture ->
                LectureResponse.from(lecture)
            }
        )
    }
}

/**
 * Creates a new lecture. Requires ADMIN role.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param services The class containing all the services containing business logic
 */
fun Route.createLectureRoute(services: Services) {
    post("/add") {
        val lectureRequest = call.receive<LectureRequest>()
        lectureRequest.validate()?.let { error ->
            return@post error.toProblem().respond(call)
        }
        val weekday = WeekDay.fromValue(lectureRequest.weekDay)
            ?: return@post RequestError.Invalid("weekday").toProblem().respond(call)
        val type = ClassType.fromValue(lectureRequest.type.uppercase())
            ?: return@post RequestError.Invalid("type").toProblem().respond(call)
        val result = services.from({lectureService}){
            createLecture(
                schoolClassId = lectureRequest.schoolClassId,
                roomId = lectureRequest.roomId,
                weekDay = weekday,
                type = type,
                startTime = lectureRequest.startTime,
                endTime = lectureRequest.endTime
            )
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { lecture ->
                LectureResponse.from(lecture)
            },
            successStatus = HttpStatusCode.Created
        )
    }
}

/**
 * Updates an existing lecture. Available to ADMIN and TEACHER roles.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param services The class containing all the services containing business logic
 */
@OptIn(ExperimentalTime::class)
fun Route.updateLectureRoute(services: Services) {
    put("/update") {
        val updateLectureRequest = call.receive<UpdateLectureRequest>()
        updateLectureRequest.validate()?.let { error ->
            return@put call.respond(error.toProblem())
        }
        val lectureIdString = call.parameters["lectureId"]
            ?: return@put RequestError.Missing("id").toProblem().respond(call)
        val lectureId = lectureIdString.toIntOrNull()
            ?: return@put RequestError.Invalid("id").toProblem().respond(call)
        val newWeekday = WeekDay.fromValue(updateLectureRequest.newWeekDay)
            ?: return@put RequestError.Invalid("newWeekday").toProblem().respond(call)
        val newType = ClassType.fromValue(updateLectureRequest.newType)
            ?: return@put RequestError.Invalid("newType").toProblem().respond(call)
        val (effectiveFrom, effectiveUntil) = updateLectureRequest.parseChangeDates()
        val result = services.from({lectureService}){
            updateLecture(
                lectureId = lectureId,
                newRoomId = updateLectureRequest.newRoomId,
                newWeekDay = newWeekday,
                newType = newType,
                newStartTime = updateLectureRequest.newStartTime,
                newEndTime = updateLectureRequest.newEndTime,
                effectiveFrom = effectiveFrom,
                effectiveUntil = effectiveUntil
            )
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { lecture ->
                LectureResponse.from(lecture)
            }
        )
    }
}

/**
 * Deletes a lecture. Requires ADMIN role.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param services The class containing all the services containing business logic
 */
fun Route.deleteLectureRoute(services: Services) {
    delete("/delete") {
        val lectureIdString = call.parameters["lectureId"]
            ?: return@delete RequestError.Missing("id").toProblem().respond(call)
        val lectureId = lectureIdString.toIntOrNull()
            ?: return@delete RequestError.Invalid("id").toProblem().respond(call)
        val result = services.from({lectureService}){
            deleteLecture(
                lectureId = lectureId,
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