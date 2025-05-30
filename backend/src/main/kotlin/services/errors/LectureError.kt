package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class LectureError {
    data object LectureAlreadyExists : LectureError()
    data object InvalidLectureClass : LectureError()
    data object InvalidLectureSubject : LectureError()
    data object InvalidLectureRoom : LectureError()
    data object InvalidLectureDate : LectureError()
    data object LectureNotFound : LectureError()
    data object LectureTimeConflict : LectureError()
    data class ConnectionDbError(val message: String?) : LectureError()

    fun toProblem(): Problem {
        return when (this) {
            LectureAlreadyExists -> Problem.conflict(
                title = "Lecture already exists",
                detail = "The lecture with the given ID already exists."
            )
            InvalidLectureClass -> Problem.badRequest(
                title = "Invalid lecture class",
                detail = "The provided lecture class is invalid."
            )
            InvalidLectureSubject -> Problem.badRequest(
                title = "Invalid lecture subject",
                detail = "The provided lecture subject is invalid."
            )
            InvalidLectureRoom -> Problem.badRequest(
                title = "Invalid lecture room",
                detail = "The provided lecture room is invalid."
            )
            InvalidLectureDate -> Problem.badRequest(
                title = "Invalid lecture date",
                detail = "The provided lecture date is invalid."
            )
            LectureNotFound -> Problem.notFound(
                title = "Lecture not found",
                detail = "The lecture with the given ID was not found."
            )
            LectureTimeConflict -> Problem.conflict(
                title = "Lecture time conflict",
                detail = "Room 101 is already booked from 09:00 to 10:30 on Monday"
            )
            is ConnectionDbError -> Problem.internalServerError(
                title = "Database Connection Error",
                detail = "Could not establish connection to the database"
            )
        }
    }
}