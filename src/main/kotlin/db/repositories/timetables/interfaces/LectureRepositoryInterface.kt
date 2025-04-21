package isel.leic.group25.db.repositories.timetables.interfaces

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import kotlin.time.Duration


interface LectureRepositoryInterface {

    fun getAllLectures(): List<Lecture>
    fun getAllLectures(limit: Int, offSet: Int): List<Lecture>

    fun createLecture(
        schoolClass: Class,
        room: Room,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture?
    fun getLecturesByRoom(roomId: Int, limit: Int, offSet: Int): List<Lecture>
    fun getLecturesByClass(classId: Int, limit: Int, offSet: Int): List<Lecture>
    fun getLecturesByType(type: ClassType): List<Lecture>
}
