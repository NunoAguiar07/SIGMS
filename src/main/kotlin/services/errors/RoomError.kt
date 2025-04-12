package isel.leic.group25.services.errors

sealed class RoomError {
    data object InvalidRoomData : RoomError()
    data object RoomAlreadyExists : RoomError()
    data object RoomNotFound : RoomError()
    data object InvalidRoomId : RoomError()
    data object InvalidRoomCapacity : RoomError()
}