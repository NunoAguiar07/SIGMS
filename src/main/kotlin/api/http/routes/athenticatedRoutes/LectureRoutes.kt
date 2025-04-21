package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.model.request.LectureRequest
import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.services.LectureService

fun Route.lectureRoutes(lectureService: LectureService) {
    route("/lectures") {
        get {
            val limit = call.parameters["limit"]
            val offset = call.parameters["offset"]
            val result = lectureService.getAllLectures(limit, offset)
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { lectures ->
                    lectures.map { LectureResponse.from(it) }
                }
            )
        }
        post {
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
}