package isel.leic.group25.services

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.interfaces.IsolationLevel
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.timetables.LectureRepository
import isel.leic.group25.services.email.EmailService
import isel.leic.group25.services.errors.LectureError
import isel.leic.group25.utils.*
import java.sql.SQLException


typealias LectureListResult = Either<LectureError, List<Lecture>>

typealias LectureResult = Either<LectureError, Lecture>

typealias DeleteLectureResult = Either<LectureError, Boolean>

class LectureService(
    private val lectureRepository: LectureRepository,
    private val transactionInterface: TransactionInterface,
    private val classRepository: ClassRepository,
    private val roomRepository: RoomRepository,
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
                    lectureRepository.getAllLectures().any {
                        it.startTime == parsedStartTime &&
                                it.endTime == parsedEndTime &&
                                it.classroom.room.id == roomId &&
                                it.schoolClass.id == schoolClassId &&
                                it.type == type &&
                                it.weekDay == weekDay }
                ) {
                    return@useTransaction failure(LectureError.LectureAlreadyExists)
                }
                val room = roomRepository.getClassRoomById(roomId) ?: return@useTransaction failure(LectureError.InvalidLectureRoom)
                val schoolClass = classRepository.findClassById(schoolClassId) ?: return@useTransaction failure(LectureError.InvalidLectureClass)
                val newLecture = lectureRepository.createLecture(schoolClass, room,type , weekDay, parsedStartTime, parsedEndTime)
                return@useTransaction success(newLecture)

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
        schoolClassId: Int,
        roomId: Int,
        type: ClassType,
        weekDay: WeekDay,
        startTime: String,
        endTime: String
    ): DeleteLectureResult {
        return runCatching {
            transactionInterface.useTransaction {
                val schoolClass = classRepository.findClassById(schoolClassId) ?: return@useTransaction failure(LectureError.InvalidLectureClass)
                val room = roomRepository.getClassRoomById(roomId) ?: return@useTransaction failure(LectureError.InvalidLectureRoom)
                val parsedStartTime = startTime.hoursAndMinutesToDuration()
                val parsedEndTime = endTime.hoursAndMinutesToDuration()
                val lecture =
                    lectureRepository.getLecture(schoolClass, room, type, weekDay, parsedStartTime, parsedEndTime )
                        ?: return@useTransaction failure(LectureError.LectureNotFound)
                if (lectureRepository.deleteLecture(lecture)) {
                    return@useTransaction success(true)
                }
                return@useTransaction failure(LectureError.LectureNotFound)
            }
        }
    }
    fun updateLecture(
        schoolClassId: Int,
        roomId: Int,
        type: ClassType,
        weekDay: WeekDay,
        startTime: String,
        endTime: String,
        newSchoolClassId: Int,
        newRoomId: Int,
        newType: ClassType,
        newWeekDay: WeekDay,
        newStartTime: String,
        newEndTime: String
    ): LectureResult {
        val newParsedStartTime = newStartTime.hoursAndMinutesToDuration()
        val newParsedEndTime = newEndTime.hoursAndMinutesToDuration()
        if (newParsedStartTime >= newParsedEndTime) {
            return failure(LectureError.InvalidLectureDate)
        }
        return runCatching {
            transactionInterface.useTransaction(IsolationLevel.SERIALIZABLE) {
                val schoolClass = classRepository.findClassById(schoolClassId) ?: return@useTransaction failure(LectureError.InvalidLectureClass)
                val classroom = roomRepository.getClassRoomById(roomId) ?: return@useTransaction failure(LectureError.InvalidLectureRoom)
                val parsedStartTime = startTime.hoursAndMinutesToDuration()
                val parsedEndTime = endTime.hoursAndMinutesToDuration()
                val lecture =
                    lectureRepository.getLecture(schoolClass, classroom, type, weekDay, parsedStartTime, parsedEndTime )
                        ?: return@useTransaction failure(LectureError.LectureNotFound)
                val newClassroom = roomRepository.getClassRoomById(newRoomId) ?: return@useTransaction failure(LectureError.InvalidLectureRoom)
                val newSchoolClass = classRepository.findClassById(newSchoolClassId) ?: return@useTransaction failure(LectureError.InvalidLectureClass)
                if (newClassroom.room.id != classroom.room.id || newWeekDay != weekDay ||
                    newParsedStartTime != parsedStartTime || newParsedEndTime != parsedEndTime) {

                    val conflictingLectures = lectureRepository.findConflictingLectures(
                        newRoomId = newClassroom.room.id,
                        newWeekDay = newWeekDay,
                        newStartTime = newParsedStartTime,
                        newEndTime = newParsedEndTime,
                        currentLecture = lecture
                    )

                    if (conflictingLectures) {
                        return@useTransaction failure(LectureError.LectureTimeConflict)
                    }
                }
                val updatedLecture = lectureRepository.updateLecture(lecture, newSchoolClass, newClassroom, newType, newWeekDay, newParsedStartTime, newParsedEndTime)
                val students = classRepository.findStudentsByClassId(schoolClassId)
                if(students.isNotEmpty()){
                    emailService.sendStudentsChangeInLecture(students.map { it.user.email }, updatedLecture)
                }
                return@useTransaction success(updatedLecture)
            }
        }
    }

}