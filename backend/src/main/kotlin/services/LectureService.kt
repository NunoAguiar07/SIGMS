package isel.leic.group25.services

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.db.repositories.interfaces.IsolationLevel
import isel.leic.group25.db.repositories.interfaces.Transactionable
import isel.leic.group25.services.email.EmailService
import isel.leic.group25.services.errors.LectureError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.hoursAndMinutesToDuration
import isel.leic.group25.utils.success
import isel.leic.group25.notifications.websocket.route.Notifications
import java.sql.SQLException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


typealias LectureListResult = Either<LectureError, List<Lecture>>

typealias LectureResult = Either<LectureError, Lecture>

typealias DeleteLectureResult = Either<LectureError, Boolean>

class LectureService(
    private val repositories: Repositories,
    private val transactionable: Transactionable,
    private val emailService: EmailService
) {

    private inline fun <T> runCatching(block: () -> Either<LectureError, T>): Either<LectureError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(LectureError.ConnectionDbError(e.message))
        }
    }

    fun getAllLectures(limit: Int, offSet: Int): LectureListResult {
        return runCatching {
            transactionable.useTransaction {
                val lectures = repositories.from({lectureRepository}){
                    getAllLectures(limit, offSet)
                }
                return@useTransaction success(lectures)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun createLecture(
        schoolClassId: Int,
        roomId: Int,
        weekDay: WeekDay,
        type: ClassType,
        startTime: String,
        endTime: String
    ): LectureResult {
        val parsedStartTime = startTime.hoursAndMinutesToDuration()
        val parsedEndTime = endTime.hoursAndMinutesToDuration()
        if (parsedStartTime >= parsedEndTime) {
            return failure(LectureError.InvalidLectureDate)
        }
        return runCatching {
            transactionable.useTransaction {
                if(
                    repositories.from({lectureRepository}){findConflictingLectures(
                        roomId,
                        weekDay,
                        parsedStartTime,
                        parsedEndTime,
                        null,
                        null,
                        null
                    )}
                ) {
                    return@useTransaction failure(LectureError.LectureTimeConflict)
                }
                val room = repositories.from({roomRepository}){getClassRoomById(roomId)}
                    ?: return@useTransaction failure(LectureError.InvalidLectureRoom)
                val schoolClass = repositories.from({classRepository}){findClassById(schoolClassId)}
                    ?: return@useTransaction failure(LectureError.InvalidLectureClass)
                val newLecture = repositories.from({lectureRepository}){
                    createLecture(schoolClass, room,type , weekDay, parsedStartTime, parsedEndTime)
                }
                return@useTransaction success(newLecture)

            }
        }
    }

    fun getLectureById(lectureId: Int): LectureResult {
        return runCatching {
            transactionable.useTransaction {
                val lecture = repositories.from({lectureRepository}){getLectureById(lectureId)}
                       ?: return@useTransaction failure(LectureError.LectureNotFound)
                return@useTransaction success(lecture)
            }
        }
    }

    fun getLecturesByRoom(roomId: Int, limit: Int, offSet: Int): LectureListResult {
        return runCatching {
            transactionable.useTransaction {
                val lectures = repositories.from({lectureRepository}){
                    getLecturesByRoom(roomId, limit, offSet)
                }
                return@useTransaction success(lectures)
            }
        }
    }

    fun getLecturesByClass(classId: Int, limit: Int, offset:Int): LectureListResult {
        return runCatching {
            transactionable.useTransaction {
                repositories.from({classRepository}){findClassById(classId)}
                    ?: return@useTransaction failure(LectureError.InvalidLectureClass)
                val lectures = repositories.from({lectureRepository}){
                    getLecturesByClass(classId, limit, offset)
                }
                return@useTransaction success(lectures)
            }
        }
    }

    fun getLecturesByType(type: ClassType): LectureListResult {
        return runCatching {
            transactionable.useTransaction {
                val lectures = repositories.from({lectureRepository}){getLecturesByType(type)}
                return@useTransaction success(lectures)
            }
        }
    }

    fun deleteLecture(
        lectureId: Int
    ): DeleteLectureResult {
        return runCatching {
            transactionable.useTransaction {
                val lecture = repositories.from({lectureRepository}){getLectureById(lectureId)}
                        ?: return@useTransaction failure(LectureError.LectureNotFound)
                if (repositories.from({lectureRepository}){deleteLecture(lecture.id)}) {
                    return@useTransaction success(true)
                }
                return@useTransaction failure(LectureError.LectureNotFound)
            }
        }
    }
    @OptIn(ExperimentalTime::class)
    fun updateLecture(
        lectureId: Int,
        newRoomId: Int?,
        newType: ClassType?,
        newWeekDay: WeekDay?,
        newStartTime: String?,
        newEndTime: String?,
        effectiveFrom: Instant?,
        effectiveUntil: Instant?,
    ): LectureResult {
        return runCatching {
            transactionable.useTransaction(IsolationLevel.SERIALIZABLE) {
                val lecture = repositories.from({lectureRepository}){getLectureById(lectureId)}
                        ?: return@useTransaction failure(LectureError.LectureNotFound)
                val currentDate = Clock.System.now()
                val newParsedStartTime = newStartTime?.hoursAndMinutesToDuration() ?: lecture.startTime
                val newParsedEndTime = newEndTime?.hoursAndMinutesToDuration() ?: lecture.endTime
                if (newParsedStartTime >= newParsedEndTime) {
                    return@useTransaction failure(LectureError.InvalidLectureDate)
                }
                val newClassroom = repositories.from({roomRepository}){
                    getClassRoomById(newRoomId ?: -1)
                }
                    ?: return@useTransaction failure(LectureError.InvalidLectureRoom)
                repositories.from({classRepository}){findClassById(lecture.schoolClass.id)}
                    ?: return@useTransaction failure(LectureError.InvalidLectureClass)
                if( effectiveUntil != null && effectiveUntil < currentDate) {
                    return@useTransaction failure(LectureError.InvalidLectureUntilDate)
                }
                val newEffectiveFrom = effectiveFrom.takeIf { it != null && it > currentDate }
                val conflictingLectures = repositories.from({lectureRepository}){
                    findConflictingLectures(
                        newRoomId = newRoomId ?: lecture.classroom.room.id,
                        newWeekDay = newWeekDay ?: lecture.weekDay,
                        newStartTime = newParsedStartTime,
                        newEndTime = newParsedEndTime,
                        effectiveFrom = newEffectiveFrom,
                        effectiveUntil = effectiveUntil,
                        currentLecture = lecture
                    )}
                if (conflictingLectures) {
                    return@useTransaction failure(LectureError.LectureTimeConflict)
                }
                val updatedLecture = repositories.from({lectureRepository}){
                    updateLecture(
                        lecture,
                        newClassroom,
                        newType ?: lecture.type,
                        newWeekDay ?: lecture.weekDay,
                        newParsedStartTime,
                        newParsedEndTime,
                        newEffectiveFrom,
                        effectiveUntil
                    )}
                val students = repositories.from({classRepository}){
                    findStudentsByClassId(lecture.schoolClass.id)
                }
                if(students.isNotEmpty()){
                    emailService.sendStudentsChangeInLecture(students.map { it.user.email }, updatedLecture)
                    Notifications.notify { Pair(students.map { it.user.id },updatedLecture ) }
                }
                return@useTransaction success(updatedLecture)
            }
        }
    }

}