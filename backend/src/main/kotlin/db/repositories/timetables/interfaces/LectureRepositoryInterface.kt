package isel.leic.group25.db.repositories.timetables.interfaces

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.timetables.LectureChange
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


interface LectureRepositoryInterface {

    fun getAllLectures(): List<Lecture>
    fun getAllLectures(limit: Int, offset: Int): List<Lecture>
    fun getAllLectureChanges(): List<LectureChange>
    fun getAllLectureChanges(limit: Int, offset: Int): List<LectureChange>
    fun getLectureById(id: Int): Lecture?
    fun getLectureChangeById(id: Int): LectureChange?
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
    fun deleteLecture(id: Int): Boolean //
    fun deleteLectureChange(id: Int): Boolean //

    @OptIn(ExperimentalTime::class)
    fun updateLecture(
        lecture: Lecture,
        newClassroom: Classroom,
        newType: ClassType,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        effectiveFrom: Instant?,
        effectiveUntil: Instant?
    ): Lecture

    @OptIn(ExperimentalTime::class)
    fun findConflictingLectures(
        newRoomId: Int,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        effectiveFrom: Instant?,
        effectiveUntil: Instant?,
        currentLecture: Lecture?
    ) : Boolean
}
