package isel.leic.group25.services.errors

import isel.leic.group25.api.exceptions.Problem

sealed class LectureError {
    data object LectureAlreadyExists : LectureError()
    data object LectureChangesFailed : LectureError()
    data object InvalidLectureId : LectureError()
    data object InvalidLectureName : LectureError()
    data object InvalidLectureClass : LectureError()
    data object InvalidLectureSubject : LectureError()
    data object InvalidLectureRoom : LectureError()
    data object InvalidLectureTime : LectureError()
    data object InvalidLectureDuration : LectureError()
    data object InvalidLectureCapacity : LectureError()
    data object InvalidLectureDate : LectureError()
    data object LectureNotFound : LectureError()

    fun toProblem(): Problem {
        return when (this) {
            LectureAlreadyExists -> Problem.conflict(
                title = "Lecture already exists",
                detail = "The lecture with the given ID already exists."
            )
            LectureChangesFailed -> Problem.internalServerError(
                title = "Lecture changes failed",
                detail = "Failed to update the lecture information."
            )
            InvalidLectureId -> Problem.badRequest(
                title = "Invalid lecture ID",
                detail = "The provided lecture ID is invalid."
            )
            InvalidLectureName -> Problem.badRequest(
                title = "Invalid lecture name",
                detail = "The provided lecture name is invalid."
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
            InvalidLectureTime -> Problem.badRequest(
                title = "Invalid lecture time",
                detail = "The provided lecture time is invalid."
            )
            InvalidLectureDuration -> Problem.badRequest(
                title = "Invalid lecture duration",
                detail = "The provided lecture duration is invalid."
            )
            InvalidLectureCapacity -> Problem.badRequest(
                title = "Invalid lecture capacity",
                detail = "The provided lecture capacity is invalid."
            )
            InvalidLectureDate -> Problem.badRequest(
                title = "Invalid lecture date",
                detail = "The provided lecture date is invalid."
            )
            LectureNotFound -> Problem.notFound(
                title = "Lecture not found",
                detail = "The lecture with the given ID was not found."
            )
        }
    }
}