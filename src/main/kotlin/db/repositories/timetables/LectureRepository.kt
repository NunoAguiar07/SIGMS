package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.lectures
import kotlinx.datetime.Instant
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*
import kotlin.time.ExperimentalTime


class LectureRepository(private val database: Database) : LectureRepositoryInterface {

    override fun getAllLectures(): List<Lecture> {
        return database.lectures.toList()
    }

    override fun getLectureById(id: Int): Lecture? {
        return database.lectures.firstOrNull { it.id eq id }
    }


    override fun createLecture(
        schoolClass: Class,
        room: Room,
        type: ClassType,
        startTime: Instant,
        endTime: Instant
    ): Lecture? {
        val newLecture = Lecture {
            this.schoolClass = schoolClass
            this.room = room
            this.type = type
            this.startTime = startTime
            this.endTime = endTime
        }
        database.lectures.add(newLecture)
        return getLectureById(newLecture.id)
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

