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


typealias LectureListResult = Either<LectureError, List<Lecture>>

typealias LectureResult = Either<LectureError, Lecture>




class LectureService(
    private val lectureRepository: LectureRepository,
    private val transactionInterface: TransactionInterface,
    private val classRepository: ClassRepository,
    private val roomRepository: RoomRepository,
    private val emailService: EmailService,
    ) {

    fun getAllLectures(limit: String?, offSet: String?): LectureListResult {
        val newLimit = limit?.toInt() ?: 20
        if (newLimit <= 0 || newLimit > 100) {
            return failure(LectureError.InvalidLectureLimit)
        }
        val newOffSet = offSet?.toInt() ?: 0
        if (newOffSet < 0) {
            return failure(LectureError.InvalidLectureOffset)
        }
        return transactionInterface.useTransaction {
            val lectures = lectureRepository.getAllLectures(newLimit, newOffSet)
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

    fun getLecturesByRoom(roomId: String?, limit: String?, offSet: String?): LectureListResult {
        if (roomId == null || roomId.toIntOrNull() == null) {
            return failure(LectureError.InvalidLectureRoom)
        }
        if (roomId.toInt() <= 0) {
            return failure(LectureError.InvalidLectureRoom)
        }
        val newLimit = limit?.toInt() ?: 20
        if (newLimit <= 0 || newLimit > 100) {
            return failure(LectureError.InvalidLectureLimit)
        }
        val newOffSet = offSet?.toInt() ?: 0
        if (newOffSet < 0) {
            return failure(LectureError.InvalidLectureOffset)
        }
        return transactionInterface.useTransaction {
            val lectures = lectureRepository.getLecturesByRoom(roomId.toInt(), newLimit, newOffSet)
            return@useTransaction success(lectures)
        }
    }

    fun getLecturesByClass(classId: String?, limit: String?, offset:String?): LectureListResult {
        val newLimit = limit?.toInt() ?: 20
        if (newLimit <= 0 || newLimit > 100) {
            return failure(LectureError.InvalidLectureLimit)
        }
        val newOffset = offset?.toInt() ?: 0
        if (newOffset < 0) {
            return failure(LectureError.InvalidLectureOffset)
        }
        if (classId == null || classId.toIntOrNull() == null) {
            return failure(LectureError.InvalidLectureSubject)
        }
        if (classId.toInt() <= 0) {
            return failure(LectureError.InvalidLectureSubject)
        }
        return transactionInterface.useTransaction {
            val lectures = lectureRepository.getLecturesByClass(classId.toInt(), newLimit, newOffset)
            return@useTransaction success(lectures)
        }
    }

    fun getLecturesByType(type: ClassType): LectureListResult {
        return transactionInterface.useTransaction {
            val lectures = lectureRepository.getLecturesByType(type)
            return@useTransaction success(lectures)
        }
    }
    fun deleteLecture(
        schoolClassId: Int,
        roomId: Int,
        type: ClassType,
        weekDay: WeekDay,
        startTime: String,
        endTime: String
    ): LectureResult {
        return transactionInterface.useTransaction {
            val schoolClass = classRepository.findClassById(schoolClassId) ?: return@useTransaction failure(LectureError.InvalidLectureClass)
            val room = roomRepository.getClassRoomById(roomId) ?: return@useTransaction failure(LectureError.InvalidLectureRoom)
            val parsedStartTime = startTime.hoursAndMinutesToDuration()
            val parsedEndTime = endTime.hoursAndMinutesToDuration()
            val lecture =
                lectureRepository.getLecture(schoolClass, room, type, weekDay, parsedStartTime, parsedEndTime )
                    ?: return@useTransaction failure(LectureError.LectureNotFound)
            if (lectureRepository.deleteLecture(lecture)) {
                return@useTransaction success(lecture)
            }
            return@useTransaction failure(LectureError.LectureNotFound)
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
        return transactionInterface.useTransaction(IsolationLevel.SERIALIZABLE) {
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