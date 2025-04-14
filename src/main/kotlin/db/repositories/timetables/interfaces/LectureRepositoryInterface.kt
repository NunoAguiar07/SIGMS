package isel.leic.group25.db.repositories.timetables.interfaces

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import kotlinx.datetime.Instant



interface LectureRepositoryInterface {
    fun getAllLectures(): List<Lecture>

    fun createLecture(
        schoolClass: Class,
        room: Room,
        weekDay: WeekDay,
        type: ClassType,
        startTime: Instant,
        endTime: Instant
    ): Lecture?
    fun getLecturesByRoom(roomId: Int): List<Lecture>
    fun getLecturesBySubject(subjectId: Int): List<Lecture>
    fun getLecturesByType(type: ClassType): List<Lecture>
}
