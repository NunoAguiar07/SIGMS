package isel.leic.group25.api.http.routes.authenticatedRoutes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.http.utils.withRoles
import isel.leic.group25.api.jwt.getUniversityIdFromPrincipal
import isel.leic.group25.api.model.response.StudyRoomCapacity.Companion.toStudyRoomCapacity
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.Services
import isel.leic.group25.utils.Either
import isel.leic.group25.websockets.hardware.route.Devices

fun Route.devicesRoutes(services: Services) {
    route("/devices") {
        retrieveCapacity(services)
        forceCapacityUpdate()
        withRoles(setOf(Role.ADMIN, Role.TECHNICAL_SERVICE)){
            retrieveDevicesWithoutRoom()
            addRoomToDevice(services)
        }
    }
}

fun Route.retrieveCapacity(services: Services) {
    get {
        val universityId = call.getUniversityIdFromPrincipal()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)
        val roomToDevice = Devices.deviceList.filter { it.roomId != 0 }.associateBy { it.roomId }
        val result = services.from({roomService}){
            getUniversityListOfRoomsByIds(universityId, roomToDevice.keys.toList())
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { rooms ->
                rooms.mapNotNull { room ->
                    val device = roomToDevice[room.id] ?: return@mapNotNull null
                    device.toStudyRoomCapacity(room.name)
                }
            }
        )
    }
}

fun Route.retrieveDevicesWithoutRoom(){
    get("/unregistered") {
        val devices = Devices.deviceList.filter { it.roomId == 0 }.map { it.toStudyRoomCapacity("")}
        call.respond(HttpStatusCode.OK, mapOf("data" to devices))
    }
}

fun Route.forceCapacityUpdate(){
    put("/update") {
        Devices.updateRoomCapacities()
        call.respond(HttpStatusCode.NoContent)
    }
}

fun Route.addRoomToDevice(services: Services){
    put("/{deviceId}/room/{roomId}") {
        val universityId = call.getUniversityIdFromPrincipal()
        val deviceId = call.parameters["deviceId"] ?: return@put RequestError.Invalid("deviceId").toProblem().respond(call)
        val roomId = call.parameters["roomId"]?.toInt() ?: return@put RequestError.Invalid("roomId").toProblem().respond(call)
        val capacity = call.request.queryParameters["capacity"]?.toIntOrNull() ?: return@put RequestError.Invalid("capacity").toProblem().respond(call)
        val result = services.from({roomService}){
            getRoomById(roomId)
        }
        when(result){
            is Either.Left -> {
                result.value.toProblem().respond(call)
            }
            is Either.Right -> {
                val room = result.value
                if(room.university.id != universityId){
                    RequestError.Invalid("roomId").toProblem().respond(call)
                }
                Devices.setDeviceRoom(deviceId, roomId, capacity)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}