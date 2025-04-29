package isel.leic.group25.db.repositories.timetables.interfaces

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import kotlin.time.Duration


interface LectureRepositoryInterface {

    fun getAllLectures(): List<Lecture>
    fun getAllLectures(limit: Int, offSet: Int): List<Lecture>
    fun getLecture(
        schoolClass: Class,
        classroom: Classroom,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture?
    fun createLecture(
        schoolClass: Class,
        classroom: Classroom,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture
    fun getLecturesByRoom(roomId: Int, limit: Int, offSet: Int): List<Lecture>
    fun getLecturesByClass(classId: Int, limit: Int, offSet: Int): List<Lecture>
    fun getLecturesByType(type: ClassType): List<Lecture>
    fun deleteLecture(lecture: Lecture): Boolean //

    fun updateLecture(
        lecture: Lecture,
        newSchoolClass: Class,
        newClassroom: Classroom,
        newType: ClassType,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration
    ): Lecture //

    fun findConflictingLectures(
        newRoomId: Int,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        currentLecture: Lecture
    ) : Boolean
}
