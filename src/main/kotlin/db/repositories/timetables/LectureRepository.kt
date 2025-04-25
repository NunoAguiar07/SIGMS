package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.lectures
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
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
        room: Room,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture? {
       val lecture = database.lectures.firstOrNull {
            (it.classId eq schoolClass.id) and
                    (it.roomId eq room.id) and
                    (it.type eq type) and
                    (it.weekDay eq weekDay) and
                    (it.startTime eq startTime) and
                    (it.endTime eq endTime)
        }
        return lecture
    }

    override fun createLecture(
        schoolClass: Class,
        room: Room,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture {
        val newLecture = Lecture {
            this.schoolClass = schoolClass
            this.room = room
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
                    (it.roomId eq lecture.room.id) and
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
        newRoom: Room,
        newType: ClassType,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration
    ): Lecture {
            lecture.schoolClass = newSchoolClass
            lecture.room = newRoom
            lecture.type = newType
            lecture.weekDay = newWeekDay
            lecture.startTime = newStartTime
            lecture.endTime = newEndTime
            database.lectures.update(lecture)
            return lecture
    }

}

