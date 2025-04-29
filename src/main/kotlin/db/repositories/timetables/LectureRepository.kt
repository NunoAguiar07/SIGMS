package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.lectures
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import kotlin.time.Duration


class LectureRepository(private val database: Database) : LectureRepositoryInterface {

    override fun getAllLectures(): List<Lecture> {
        return database.lectures.toList()
    }

    override fun getAllLectures(limit:Int, offSet: Int): List<Lecture> {
        return database.lectures.drop(offSet).take(limit).toList()
    }

    override fun getLecture(
        schoolClass: Class,
        classroom: Classroom,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture? {
       val lecture = database.lectures.firstOrNull {
            (it.classId eq schoolClass.id) and
                    (it.roomId eq classroom.room.id) and
                    (it.type eq type) and
                    (it.weekDay eq weekDay) and
                    (it.startTime eq startTime) and
                    (it.endTime eq endTime)
        }
        return lecture
    }

    override fun createLecture(
        schoolClass: Class,
        classroom: Classroom,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture {
        val newLecture = Lecture {
            this.schoolClass = schoolClass
            this.classroom = classroom
            this.type = type
            this.weekDay = weekDay
            this.startTime = startTime
            this.endTime = endTime
        }
        database.lectures.add(newLecture)
        return newLecture
    }

    override fun getLecturesByRoom(roomId: Int, limit:Int, offSet:Int): List<Lecture> {
        return database.lectures.filter { it.roomId eq roomId }.drop(offSet).take(limit).toList()
    }

    override fun getLecturesByClass(classId: Int, limit:Int, offSet:Int): List<Lecture> {
        return database.lectures.filter { it.classId eq classId }.drop(offSet).take(limit).toList()
    }

    override fun getLecturesByType(type: ClassType): List<Lecture> {
        return database.lectures.filter { it.type eq type }.map { it }
    }

    override fun deleteLecture(lecture: Lecture): Boolean {
         val deletedLecture = database.lectures.removeIf {
            (it.classId eq lecture.schoolClass.id) and
                    (it.roomId eq lecture.classroom.room.id) and
                    (it.type eq lecture.type) and
                    (it.weekDay eq lecture.weekDay) and
                    (it.startTime eq lecture.startTime) and
                    (it.endTime eq lecture.endTime)
        }
        return deletedLecture == 0
    }

    override fun updateLecture(
        lecture: Lecture,
        newSchoolClass: Class,
        newClassroom: Classroom,
        newType: ClassType,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration
    ): Lecture {
            lecture.schoolClass = newSchoolClass
            lecture.classroom = newClassroom
            lecture.type = newType
            lecture.weekDay = newWeekDay
            lecture.startTime = newStartTime
            lecture.endTime = newEndTime
            database.lectures.update(lecture)
            return lecture
    }

    override fun findConflictingLectures(
        newRoomId: Int,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        currentLecture: Lecture
    ): Boolean {
        return database.lectures.any { existingLecture ->
            // Check if in same room and same day
            (existingLecture.roomId eq newRoomId) and
                    (existingLecture.weekDay eq newWeekDay) and
                    // Exclude the lecture we're updating
                    !(
                        (existingLecture.classId eq currentLecture.schoolClass.id) and
                        (existingLecture.roomId eq currentLecture.classroom.room.id) and
                        (existingLecture.type eq currentLecture.type) and
                        (existingLecture.weekDay eq currentLecture.weekDay) and
                        (existingLecture.startTime eq currentLecture.startTime) and
                        (existingLecture.endTime eq currentLecture.endTime)
                    ) and
                    // Check for time overlap
                    ((newStartTime greaterEq existingLecture.startTime) and (newStartTime less existingLecture.endTime)) or // New starts during existing
                    ((newEndTime greater  existingLecture.startTime) and (newEndTime lessEq existingLecture.endTime)) or    // New ends during existing
                    ((newStartTime lessEq  existingLecture.startTime) and (newEndTime greaterEq existingLecture.endTime))   // New completely contains existing
        }
    }

}

