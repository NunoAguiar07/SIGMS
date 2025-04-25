package mocks.repositories.timetables

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import kotlin.time.Duration

class MockLectureRepository : LectureRepositoryInterface {
    private val lectures = mutableListOf<Lecture>()

    override fun getAllLectures(): List<Lecture> {
        return lectures.toList()
    }

    override fun getAllLectures(limit: Int, offSet: Int): List<Lecture> {
        return lectures.drop(offSet).take(limit)
    }

    override fun getLecture(
        schoolClass: Class,
        room: Room,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture? {
        return lectures.firstOrNull {
            it.schoolClass.id == schoolClass.id &&
                    it.room.id == room.id &&
                    it.type == type &&
                    it.weekDay == weekDay &&
                    it.startTime == startTime &&
                    it.endTime == endTime
        }
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
        lectures.add(newLecture)
        return newLecture
    }

    override fun getLecturesByRoom(roomId: Int, limit: Int, offSet: Int): List<Lecture> {
        return lectures.filter { it.room.id == roomId }
            .drop(offSet)
            .take(limit)
    }

    override fun getLecturesByClass(classId: Int, limit: Int, offSet: Int): List<Lecture> {
        return lectures.filter { it.schoolClass.id == classId }
            .drop(offSet)
            .take(limit)
    }

    override fun getLecturesByType(type: ClassType): List<Lecture> {
        return lectures.filter { it.type == type }
    }

    override fun deleteLecture(lecture: Lecture): Boolean {
        return lectures.removeIf {
                   it.schoolClass == lecture.schoolClass &&
                    it.room == lecture.room &&
                    it.type == lecture.type &&
                    it.startTime == lecture.startTime &&
                    it.endTime == lecture.endTime
        }
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
        return lecture.apply {
            schoolClass = newSchoolClass
            room = newRoom
            type = newType
            weekDay = newWeekDay
            startTime = newStartTime
            endTime = newEndTime
        }
    }

    fun clear() {
        lectures.clear()
    }
}