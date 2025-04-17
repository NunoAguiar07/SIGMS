package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.exceptions.toProblem
import isel.leic.group25.api.model.request.RoomRequest
import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.api.model.response.RoomResponse
import isel.leic.group25.services.LectureService
import isel.leic.group25.services.RoomService

fun Route.roomRoutes(
    roomService: RoomService,
    lectureService: LectureService
) {
    route("/rooms") {
        get {
            val result = roomService.getAllRooms()
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
                    val id = call.parameters["roomId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val result = lectureService.getLecturesByRoom(id)
                    call.respondEither(
                        either = result,
                        transformError = { error -> error.toProblem() },
                        transformSuccess = { lectures ->
                            lectures.map { LectureResponse.from(it) }
                        }
                    )
                }
            }
        }
    }
}