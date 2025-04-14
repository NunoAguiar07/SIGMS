package isel.leic.group25.services.errors

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
}