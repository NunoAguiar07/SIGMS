package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class TeacherRoomError {
    data object TeacherNotFound : TeacherRoomError()
    data object RoomNotFound : TeacherRoomError()

    fun toProblem(): Problem {
        return when (this) {
            TeacherNotFound -> Problem.notFound(
                title = "Teacher not found"
            )
            RoomNotFound -> Problem.notFound(
                title = "Room not found",
                detail = "The room with the given ID was not found."
            )
        }
    }
}