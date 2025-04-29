package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.http.utils.withRole
import isel.leic.group25.api.http.utils.withRoles
import isel.leic.group25.api.model.request.LectureRequest
import isel.leic.group25.api.model.request.UpdateLectureRequest
import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.LectureService

/**
 * Defines lecture management routes including creation, retrieval, updating and deletion.
 * Routes have role-based access control with different operations available to different roles.
 *
 * @receiver Route The Ktor route to which these endpoints will be added
 * @param lectureService Service handling lecture business logic
 */
fun Route.lectureRoutes(lectureService: LectureService) {
    route("/lectures") {
        getAllLecturesRoute(lectureService)
        withRole(Role.ADMIN){
            createLectureRoute(lectureService)
            deleteLectureRoute(lectureService)
        }
        withRoles(setOf(Role.ADMIN, Role.TEACHER)){
            updateLectureRoute(lectureService)
        }
    }
}

/**
 * Retrieves all lectures with optional pagination parameters.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param lectureService Service handling lecture retrieval logic
 */
fun Route.getAllLecturesRoute(lectureService: LectureService) {
    get {
        val limit = call.queryParameters["limit"]
        val offset = call.queryParameters["offset"]
        val result = lectureService.getAllLectures(limit, offset)
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
 * Creates a new lecture. Requires ADMIN role.
 *
 * @receiver Route The Ktor route for this endpoint
 * @param lectureService Service handling lecture creation logic
 */
fun Route.createLectureRoute(lectureService: LectureService) {
    post("/add") {
        val lectureRequest = call.receive<LectureRequest>()
        val result = lectureService.createLecture(
            schoolClassId = lectureRequest.schoolClassId,
            roomId = lectureRequest.roomId,
            weekDay = lectureRequest.weekDay,
            type = lectureRequest.type,
            startTime = lectureRequest.startTime,
            endTime = lectureRequest.endTime
        )
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
 * @param lectureService Service handling lecture update logic
 */
fun Route.updateLectureRoute(lectureService: LectureService) {
    put("/update") {
        val updateLectureRequest = call.receive<UpdateLectureRequest>()
        val result = lectureService.updateLecture(
            schoolClassId = updateLectureRequest.schoolClassId,
            roomId = updateLectureRequest.roomId,
            weekDay = updateLectureRequest.weekDay,
            type = updateLectureRequest.type,
            startTime = updateLectureRequest.startTime,
            endTime = updateLectureRequest.endTime,
            newSchoolClassId = updateLectureRequest.newSchoolClassId,
            newRoomId = updateLectureRequest.newRoomId,
            newWeekDay = updateLectureRequest.newWeekDay,
            newType = updateLectureRequest.newType,
            newStartTime = updateLectureRequest.newStartTime,
            newEndTime = updateLectureRequest.newEndTime
        )
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
 * @param lectureService Service handling lecture deletion logic
 */
fun Route.deleteLectureRoute(lectureService: LectureService) {
    delete("/delete") {
        val lectureRequest = call.receive<LectureRequest>()
        val result = lectureService.deleteLecture(
            schoolClassId = lectureRequest.schoolClassId,
            roomId = lectureRequest.roomId,
            weekDay = lectureRequest.weekDay,
            type = lectureRequest.type,
            startTime = lectureRequest.startTime,
            endTime = lectureRequest.endTime
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