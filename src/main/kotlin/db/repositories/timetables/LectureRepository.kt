package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.lectures
import org.ktorm.database.Database
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

    override fun createLecture(
        schoolClass: Class,
        room: Room,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture? {
        val newLecture = Lecture {
            this.schoolClass = schoolClass
            this.room = room
            this.type = type
            this.weekDay = weekDay
            this.startTime = startTime
            this.endTime = endTime
        }
        database.lectures.add(newLecture)
        return getLecturesByRoom(room.id, 1, 0)
            .firstOrNull { it.startTime == startTime && it.endTime == endTime && it.room.id == room.id && it.schoolClass.id == schoolClass.id }
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
}

