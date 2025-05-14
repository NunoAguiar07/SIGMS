package isel.leic.group25.services

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.interfaces.IsolationLevel
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.rooms.interfaces.RoomRepositoryInterface
import isel.leic.group25.db.repositories.timetables.interfaces.ClassRepositoryInterface
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import isel.leic.group25.services.email.EmailService
import isel.leic.group25.services.errors.LectureError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.hoursAndMinutesToDuration
import isel.leic.group25.utils.success
import java.sql.SQLException
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


typealias LectureListResult = Either<LectureError, List<Lecture>>

typealias LectureResult = Either<LectureError, Lecture>

typealias DeleteLectureResult = Either<LectureError, Boolean>

class LectureService(
    private val lectureRepository: LectureRepositoryInterface,
    private val transactionInterface: TransactionInterface,
    private val classRepository: ClassRepositoryInterface,
    private val roomRepository: RoomRepositoryInterface,
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
            transactionInterface.useTransaction {
                val lectures = lectureRepository.getAllLectures(limit, offSet)
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
            transactionInterface.useTransaction {
                if(
                    lectureRepository.findConflictingLectures(
                        roomId,
                        weekDay,
                        parsedStartTime,
                        parsedEndTime,
                        null,
                        null,
                        null
                    )
                ) {
                    return@useTransaction failure(LectureError.LectureTimeConflict)
                }
                val room = roomRepository.getClassRoomById(roomId) ?: return@useTransaction failure(LectureError.InvalidLectureRoom)
                val schoolClass = classRepository.findClassById(schoolClassId) ?: return@useTransaction failure(LectureError.InvalidLectureClass)
                val newLecture = lectureRepository.createLecture(schoolClass, room,type , weekDay, parsedStartTime, parsedEndTime)
                return@useTransaction success(newLecture)

            }
        }
    }

    fun getLectureById(lectureId: Int): LectureResult {
        return runCatching {
            transactionInterface.useTransaction {
                val lecture = lectureRepository.getLectureById(lectureId)
                       ?: return@useTransaction failure(LectureError.LectureNotFound)
                return@useTransaction success(lecture)
            }
        }
    }

    fun getLecturesByRoom(roomId: Int, limit: Int, offSet: Int): LectureListResult {
        return runCatching {
            transactionInterface.useTransaction {
                val lectures = lectureRepository.getLecturesByRoom(roomId, limit, offSet)
                return@useTransaction success(lectures)
            }
        }
    }

    fun getLecturesByClass(classId: Int, limit: Int, offset:Int): LectureListResult {
        return runCatching {
            transactionInterface.useTransaction {
                classRepository.findClassById(classId) ?: return@useTransaction failure(LectureError.InvalidLectureClass)
                val lectures = lectureRepository.getLecturesByClass(classId, limit, offset)
                return@useTransaction success(lectures)
            }
        }
    }

    fun getLecturesByType(type: ClassType): LectureListResult {
        return runCatching {
            transactionInterface.useTransaction {
                val lectures = lectureRepository.getLecturesByType(type)
                return@useTransaction success(lectures)
            }
        }
    }

    fun deleteLecture(
        lectureId: Int
    ): DeleteLectureResult {
        return runCatching {
            transactionInterface.useTransaction {
                val lecture = lectureRepository.getLectureById(lectureId)
                        ?: return@useTransaction failure(LectureError.LectureNotFound)
                if (lectureRepository.deleteLecture(lecture.id)) {
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
            transactionInterface.useTransaction(IsolationLevel.SERIALIZABLE) {
                val lecture = lectureRepository.getLectureById(lectureId)
                        ?: return@useTransaction failure(LectureError.LectureNotFound)
                val newParsedStartTime = newStartTime?.hoursAndMinutesToDuration() ?: lecture.startTime
                val newParsedEndTime = newEndTime?.hoursAndMinutesToDuration() ?: lecture.endTime
                if (newParsedStartTime >= newParsedEndTime) {
                    return@useTransaction failure(LectureError.InvalidLectureDate)
                }
                val newClassroom = roomRepository.getClassRoomById(newRoomId ?: -1)
                    ?: return@useTransaction failure(LectureError.InvalidLectureRoom)
                classRepository.findClassById(lecture.schoolClass.id)
                    ?: return@useTransaction failure(LectureError.InvalidLectureClass)
                val conflictingLectures = lectureRepository.findConflictingLectures(
                    newRoomId = newRoomId ?: lecture.classroom.room.id,
                    newWeekDay = newWeekDay ?: lecture.weekDay,
                    newStartTime = newParsedStartTime,
                    newEndTime = newParsedEndTime,
                    effectiveFrom = effectiveFrom,
                    effectiveUntil = effectiveUntil,
                    currentLecture = lecture
                )
                if (conflictingLectures) {
                    return@useTransaction failure(LectureError.LectureTimeConflict)
                }
                val updatedLecture = lectureRepository.updateLecture(
                    lecture,
                    newClassroom,
                    newType ?: lecture.type,
                    newWeekDay ?: lecture.weekDay,
                    newParsedStartTime,
                    newParsedEndTime,
                    effectiveFrom,
                    effectiveUntil
                )
                val students = classRepository.findStudentsByClassId(lecture.schoolClass.id)
                if(students.isNotEmpty()){
                    emailService.sendStudentsChangeInLecture(students.map { it.user.email }, updatedLecture)
                }
                return@useTransaction success(updatedLecture)
            }
        }
    }

}