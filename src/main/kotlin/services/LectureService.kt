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
import isel.leic.group25.utils.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.sql.SQLException
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


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
    fun updateLecture(
        lectureId: Int,
        newSchoolClassId: Int,
        newRoomId: Int,
        newType: ClassType,
        newWeekDay: WeekDay,
        newStartTime: String,
        newEndTime: String,
        numberOfWeeks: Int
    ): LectureResult {
        val newParsedStartTime = newStartTime.hoursAndMinutesToDuration()
        val newParsedEndTime = newEndTime.hoursAndMinutesToDuration()
        if (newParsedStartTime >= newParsedEndTime) {
            return failure(LectureError.InvalidLectureDate)
        }
        return runCatching {
            transactionInterface.useTransaction(IsolationLevel.SERIALIZABLE) {
                val lecture = lectureRepository.getLectureById(lectureId)
                        ?: return@useTransaction failure(LectureError.LectureNotFound)
                val now = Clock.System.now()
                val today = now.toLocalDateTime(TimeZone.currentSystemDefault())
                val currentDayOfWeek = today.dayOfWeek.value
                val currentTimeDuration = today.hour.hours + today.minute.minutes + today.second.seconds
                val adjustedNumberOfWeeks = when {
                    lecture.weekDay.value < currentDayOfWeek -> numberOfWeeks + 1
                    lecture.weekDay.value == currentDayOfWeek &&
                            lecture.endTime < currentTimeDuration
                                -> numberOfWeeks + 1
                    else -> numberOfWeeks
                }
                val newClassroom = roomRepository.getClassRoomById(newRoomId)
                    ?: return@useTransaction failure(LectureError.InvalidLectureRoom)
                classRepository.findClassById(newSchoolClassId)
                    ?: return@useTransaction failure(LectureError.InvalidLectureClass)
                val conflictingLectures = lectureRepository.findConflictingLectures(
                    newRoomId = newRoomId,
                    newWeekDay = newWeekDay,
                    newStartTime = newParsedStartTime,
                    newEndTime = newParsedEndTime,
                    currentLecture = lecture
                )
                if (conflictingLectures) {
                    return@useTransaction failure(LectureError.LectureTimeConflict)
                }
                val updatedLecture = lectureRepository.updateLecture(
                    lecture, newClassroom, newType, newWeekDay,
                    newParsedStartTime, newParsedEndTime, adjustedNumberOfWeeks
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