package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.timetables.LectureChange
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import isel.leic.group25.db.repositories.utils.withDatabase
import isel.leic.group25.db.tables.Tables.Companion.lectures
import isel.leic.group25.db.tables.Tables.Companion.lectureChanges
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import kotlin.time.Duration


class LectureRepository(private val database: Database) : LectureRepositoryInterface {
    override fun getLectureById(id: Int): Lecture? = withDatabase {
        val baseLecture = database.lectures.firstOrNull { it.id eq id } ?: return null
        val activeChange = database.lectureChanges.firstOrNull {
            (it.lectureId eq id) and (it.remainingWeeks gt 0)
        }
        if (activeChange != null) {
            baseLecture.weekDay = activeChange.newWeekDay
            baseLecture.startTime = activeChange.newStartTime
            baseLecture.endTime = activeChange.newEndTime
            baseLecture.classroom = activeChange.newClassroom
        }
        return baseLecture
    }

    override fun getAllLectures(): List<Lecture> = withDatabase {
        return getAllLecturesInternal(database.lectures.toList())
    }

    override fun getAllLectures(limit: Int, offset: Int): List<Lecture> = withDatabase {
        return getAllLecturesInternal(database.lectures.drop(offset).take(limit).toList())
    }

    override fun getLecture(
        schoolClass: Class,
        classroom: Classroom,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture? = withDatabase {
       val lecture = database.lectures.firstOrNull {
            (it.classId eq schoolClass.id) and
                    (it.roomId eq classroom.room.id) and
                    (it.type eq type) and
                    (it.weekDay eq weekDay) and
                    (it.startTime eq startTime) and
                    (it.endTime eq endTime)
        }
        if(lecture == null) return null
        val activeChange = database.lectureChanges.firstOrNull {
            (it.lectureId eq lecture.id) and (it.remainingWeeks gt 0)
        }
        if (activeChange != null) {
            lecture.weekDay = activeChange.newWeekDay
            lecture.startTime = activeChange.newStartTime
            lecture.endTime = activeChange.newEndTime
            lecture.classroom = activeChange.newClassroom
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
    ): Lecture = withDatabase {
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

    override fun getLecturesByRoom(roomId: Int, limit:Int, offSet:Int): List<Lecture> = withDatabase {
        val lectures = database.lectures.filter { it.roomId eq roomId }.drop(offSet).take(limit).toList()
        return getAllLecturesInternal(lectures)
    }

    override fun getLecturesByClass(classId: Int, limit:Int, offSet:Int): List<Lecture> = withDatabase {
        val lectures =  database.lectures.filter { it.classId eq classId }.drop(offSet).take(limit).toList()
        return getAllLecturesInternal(lectures)
    }

    override fun getLecturesByType(type: ClassType): List<Lecture> = withDatabase {
        val lectures = database.lectures.filter { it.type eq type }.map { it }
        return getAllLecturesInternal(lectures)
    }

    override fun deleteLecture(id: Int): Boolean = withDatabase {
        return database.lectures.removeIf { it.id eq id } > 0
    }

    override fun deleteLectureChange(id: Int): Boolean {
        return database.lectureChanges.removeIf { it.lectureId eq id } > 0
    }

    override fun updateLecture(
        lecture: Lecture,
        newClassroom: Classroom,
        newType: ClassType,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        numberOfWeeks: Int
    ): Lecture = withDatabase {
        lecture.classroom = newClassroom
        lecture.type = newType
        lecture.weekDay = newWeekDay
        lecture.startTime = newStartTime
        lecture.endTime = newEndTime
        if(numberOfWeeks == 0) {
            database.lectures.update(lecture)
        } else {
            val lectureChange = database.lectureChanges.firstOrNull { it.lectureId eq lecture.id }
            if (lectureChange == null) {
                val newLectureChange = LectureChange {
                    this.lecture = lecture
                    this.newClassroom = newClassroom
                    this.newWeekDay = newWeekDay
                    this.newStartTime = newStartTime
                    this.newEndTime = newEndTime
                    this.remainingWeeks = numberOfWeeks
                }
                database.lectureChanges.add(newLectureChange)
            } else {
                lectureChange.newClassroom = newClassroom
                lectureChange.newWeekDay = newWeekDay
                lectureChange.newStartTime = newStartTime
                lectureChange.newEndTime = newEndTime
                lectureChange.remainingWeeks = numberOfWeeks
                database.lectureChanges.update(lectureChange)
            }
        }
        return lecture
    }

    override fun findConflictingLectures(
        newRoomId: Int,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        currentLecture: Lecture?
    ): Boolean = withDatabase {
        val hasLectureConflict =  database.lectures.any { existingLecture ->
            // Check if in same room and same day
            (existingLecture.roomId eq newRoomId) and
            (existingLecture.weekDay eq newWeekDay) and
            // Exclude the lecture
            ((currentLecture == null) or (existingLecture.id neq (currentLecture?.id ?: 0))) and
            !database.lectureChanges.any {
                (it.lectureId eq existingLecture.id) and (it.remainingWeeks gt 0)
            } and
            // Check for time overlap
            ((newStartTime greaterEq existingLecture.startTime) and (newStartTime less existingLecture.endTime)) or // New starts during existing
            ((newEndTime greater  existingLecture.startTime) and (newEndTime lessEq existingLecture.endTime)) or    // New ends during existing
            ((newStartTime lessEq  existingLecture.startTime) and (newEndTime greaterEq existingLecture.endTime))   // New completely contains existing
        }

        val hasChangeConflict = database.lectureChanges.any { change ->
            // Only consider active changes
            (change.remainingWeeks gt 0) and
            // Same room and day
            (change.newRoomId eq newRoomId) and
            (change.newWeekDay eq newWeekDay) and
            // Exclude changes for current lecture if provided
            ((currentLecture == null) or (change.lectureId neq (currentLecture?.id ?: 0)))
            // Time overlap conditions
            (
                ((newStartTime greaterEq change.newStartTime) and (newStartTime less change.newEndTime)) or // New starts during changed
                ((newEndTime greater change.newStartTime) and (newEndTime lessEq change.newEndTime)) or     // New ends during changed
                ((newStartTime lessEq change.newStartTime) and (newEndTime greaterEq change.newEndTime))    // New contains changed
            )
        }
        return hasLectureConflict || hasChangeConflict
    }

    private fun getAllLecturesInternal(lectures: List<Lecture>): List<Lecture> = withDatabase {
        val changedLectures = database.lectureChanges.filter { it.remainingWeeks gt 0 }
        lectures.forEach { lecture ->
            changedLectures.firstOrNull { it.lectureId eq lecture.id }?.let { change ->
                lecture.weekDay = change.newWeekDay
                lecture.startTime = change.newStartTime
                lecture.endTime = change.newEndTime
                lecture.classroom = change.newClassroom
            }
        }
        return lectures
    }
}

