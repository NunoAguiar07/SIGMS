package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class RoomError {
    data object RoomAlreadyExists : RoomError()
    data object RoomNotFound : RoomError()
    data object InvalidRoomId : RoomError()
    data object InvalidRoomCapacity : RoomError()
    data object InvalidRoomLimit : RoomError()
    data object InvalidRoomOffSet : RoomError()
    data object InvalidRoomType : RoomError()

    fun toProblem(): Problem {
        return when (this) {
            RoomNotFound -> Problem.notFound(
                title = "Room not found",
                detail = "The room with the given ID was not found."
            )
            RoomAlreadyExists -> Problem.conflict(
                title = "Room already exists",
                detail = "The room with the given name already exists."
            )
            InvalidRoomId -> Problem.badRequest(
                title = "Invalid room ID",
                detail = "The provided room ID is invalid."
            )
            InvalidRoomCapacity -> Problem.badRequest(
                title = "Invalid room capacity",
                detail = "The provided room capacity is invalid."
            )
            InvalidRoomLimit -> Problem.badRequest(
                title = "Invalid room limit",
                detail = "The provided room limit is invalid."
            )
            InvalidRoomOffSet -> Problem.badRequest(
                title = "Invalid room offset",
                detail = "The provided room offset is invalid."
            )
            InvalidRoomType -> Problem.badRequest(
                title = "Invalid room type",
                detail = "The provided room type is invalid."
            )
        }
    }
}