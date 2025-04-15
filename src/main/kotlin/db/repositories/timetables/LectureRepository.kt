package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
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


    override fun createLecture(
        schoolClass: Class,
        room: Room,
        type: ClassType,
        startTime: Duration,
        endTime: Duration
    ): Lecture? {
        val newLecture = Lecture {
            this.schoolClass = schoolClass
            this.room = room
            this.type = type
            this.startTime = startTime
            this.endTime = endTime
        }
        database.lectures.add(newLecture)
        return getLecturesByRoom(room.id)
            .firstOrNull { it.startTime == startTime && it.endTime == endTime && it.room.id == room.id && it.schoolClass.id == schoolClass.id }
    }

    override fun getLecturesByRoom(roomId: Int): List<Lecture> {
        return database.lectures.filter { it.roomId eq roomId }.map { it }
    }

    override fun getLecturesBySubject(subjectId: Int): List<Lecture> {
        return database.lectures.filter { it.classId eq subjectId }.map { it }
    }

    override fun getLecturesByType(type: ClassType): List<Lecture> {
        return database.lectures.filter { it.type eq type }.map { it }
    }
}

