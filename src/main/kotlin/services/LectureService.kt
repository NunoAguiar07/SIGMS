package isel.leic.group25.services

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.timetables.LectureRepository
import isel.leic.group25.services.errors.LectureError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import kotlinx.datetime.Instant

typealias LectureListResult = Either<LectureError, List<Lecture>>

typealias LectureResult = Either<LectureError, Lecture>




class LectureService(private val lectureRepository: LectureRepository,
                     private val transactionInterface: TransactionInterface,) {

    fun getAllLectures(): LectureListResult {
        return transactionInterface.useTransaction {
            val lectures = lectureRepository.getAllLectures()
            return@useTransaction success(lectures)
        }
    }

    fun createLecture(
        schoolClass: Class,
        room: Room,
        weekDay: WeekDay,
        type: ClassType,
        startTime: Instant,
        endTime: Instant
    ): LectureResult {
        if (startTime >= endTime) {
            return failure(LectureError.InvalidLectureDate)
        }
        return transactionInterface.useTransaction {
            if(
                lectureRepository.getAllLectures().any { it.startTime == startTime && it.endTime == endTime && it.room.id == room.id && it.schoolClass.id == schoolClass.id && it.type == type && it.weekDay == weekDay }
            ) {
                return@useTransaction failure(LectureError.LectureAlreadyExists)
            }
           val newLecture = lectureRepository.createLecture(schoolClass, room, weekDay, type, startTime, endTime)
               ?: return@useTransaction failure(LectureError.LectureNotFound)
            return@useTransaction success(newLecture)
        }
    }

    fun getLecturesByRoom(roomId: Int): LectureListResult {
        if (roomId <= 0) {
            return failure(LectureError.InvalidLectureRoom)
        }
        return transactionInterface.useTransaction {
            val lectures = lectureRepository.getLecturesByRoom(roomId)
            return@useTransaction success(lectures)
        }
    }

    fun getLecturesBySubject(subjectId: Int): LectureListResult {
        if (subjectId <= 0) {
            return failure(LectureError.InvalidLectureSubject)
        }
        return transactionInterface.useTransaction {
            val lectures = lectureRepository.getLecturesBySubject(subjectId)
            return@useTransaction success(lectures)
        }
    }

    fun getLecturesByType(type: ClassType): LectureListResult {
        return transactionInterface.useTransaction {
            val lectures = lectureRepository.getLecturesByType(type)
            return@useTransaction success(lectures)
        }
    }

}