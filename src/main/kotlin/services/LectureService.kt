package isel.leic.group25.services

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.timetables.LectureRepository
import isel.leic.group25.services.errors.LectureError
import isel.leic.group25.utils.*
import kotlinx.datetime.Instant
import kotlin.time.Duration


typealias LectureListResult = Either<LectureError, List<Lecture>>

typealias LectureResult = Either<LectureError, Lecture>




class LectureService(private val lectureRepository: LectureRepository,
                     private val transactionInterface: TransactionInterface,
                     private val classRepository: ClassRepository,
                     private val roomRepository: RoomRepository) {

    fun getAllLectures(): LectureListResult {
        return transactionInterface.useTransaction {
            val lectures = lectureRepository.getAllLectures()
            return@useTransaction success(lectures)
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

        return transactionInterface.useTransaction {
            if(
                lectureRepository.getAllLectures().any { it.startTime == parsedStartTime && it.endTime == parsedEndTime && it.room.id == roomId && it.schoolClass.id == schoolClassId && it.type == type && it.weekDay == weekDay }
            ) {
                return@useTransaction failure(LectureError.LectureAlreadyExists)
            }
            val room = roomRepository.getRoomById(roomId) ?: return@useTransaction failure(LectureError.InvalidLectureRoom)
            val schoolClass = classRepository.findClassById(schoolClassId) ?: return@useTransaction failure(LectureError.InvalidLectureClass)
           val newLecture = lectureRepository.createLecture(schoolClass, room,type , weekDay, parsedStartTime, parsedEndTime)
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