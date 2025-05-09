package mocks.repositories.timetables

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.LectureChange
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import kotlin.time.Duration

class MockLectureRepository : LectureRepositoryInterface {
    private val lectures = mutableListOf<Lecture>()
    private val lectureChanges = mutableListOf<LectureChange>()

    override fun getLectureById(id: Int): Lecture? {
        val lecture = lectures.firstOrNull { it.id == id } ?: return null
        return applyActiveChanges(lecture)
    }

    override fun getAllLectures(): List<Lecture> {
        return lectures.map { lecture ->
            applyActiveChanges(lecture)
        }
    }

    override fun getAllLectures(limit: Int, offset: Int): List<Lecture> {
        return lectures.drop(offset).take(limit).map { lecture ->
            applyActiveChanges(lecture)
        }
    }

    override fun getLecture(
        schoolClass: Class,
        classroom: Classroom,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture? {
        val lecture = lectures.firstOrNull {
            it.schoolClass.id == schoolClass.id &&
                    it.classroom.room.id == classroom.room.id &&
                    it.type == type &&
                    it.weekDay == weekDay &&
                    it.startTime == startTime &&
                    it.endTime == endTime
        } ?: return null

        val activeChange = lectureChanges.firstOrNull { it.lecture.id == lecture.id && it.remainingWeeks > 0 }
        return if (activeChange != null) {
            lecture.apply {
                this.weekDay = activeChange.newWeekDay
                this.startTime = activeChange.newStartTime
                this.endTime = activeChange.newEndTime
                this.classroom = activeChange.newClassroom
            }
        } else {
            lecture
        }
    }

    override fun createLecture(
        schoolClass: Class,
        classroom: Classroom,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture {
        val newLecture = Lecture {
            this.schoolClass = schoolClass
            this.classroom = classroom
            this.type = type
            this.weekDay = weekDay
            this.startTime = startTime
            this.endTime = endTime
        }
        lectures.add(newLecture)
        return newLecture
    }

    override fun getLecturesByRoom(roomId: Int, limit: Int, offSet: Int): List<Lecture> {
        return lectures.filter { it.classroom.room.id == roomId }
            .drop(offSet)
            .take(limit)
            .map { applyActiveChanges(it) }
    }

    override fun getLecturesByClass(classId: Int, limit: Int, offSet: Int): List<Lecture> {
        return lectures.filter { it.schoolClass.id == classId }
            .drop(offSet)
            .take(limit)
            .map { applyActiveChanges(it) }
    }

    override fun getLecturesByType(type: ClassType): List<Lecture> {
        return lectures.filter { it.type == type }.map { applyActiveChanges(it) }
    }

    override fun deleteLecture(id: Int): Boolean {
        return lectures.removeIf { it.id == id }
    }

    override fun deleteLectureChange(id: Int): Boolean {
        return lectureChanges.removeIf { it.lecture.id == id }
    }

    override fun updateLecture(
        lecture: Lecture,
        newClassroom: Classroom,
        newType: ClassType,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        numberOfWeeks: Int
    ): Lecture {
        lecture.classroom = newClassroom
        lecture.type = newType
        lecture.weekDay = newWeekDay
        lecture.startTime = newStartTime
        lecture.endTime = newEndTime

        if (numberOfWeeks == 0) {
            // Update directly
            lectures.replaceAll { if (it.id == lecture.id) lecture else it }
        } else {
            val existingChange = lectureChanges.firstOrNull { it.lecture.id == lecture.id }
            if (existingChange == null) {
                lectureChanges.add(LectureChange {
                    this.newClassroom = newClassroom
                    this.newWeekDay = newWeekDay
                    this.newStartTime = newStartTime
                    this.newEndTime = newEndTime
                    this.remainingWeeks = numberOfWeeks
                })
            } else {
                existingChange.newClassroom = newClassroom
                existingChange.newWeekDay = newWeekDay
                existingChange.newStartTime = newStartTime
                existingChange.newEndTime = newEndTime
                existingChange.remainingWeeks = numberOfWeeks
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
    ): Boolean {
        // Check conflicts in regular lectures
        val lectureConflict = lectures.any { lecture ->
            lecture.classroom.room.id == newRoomId &&
                    lecture.weekDay == newWeekDay &&
                    (currentLecture == null || lecture.id != currentLecture.id) &&
                    !lectureChanges.any { it.lecture.id == lecture.id && it.remainingWeeks > 0 } &&
                    (timeOverlap(
                        newStartTime, newEndTime,
                        lecture.startTime, lecture.endTime
                    ))
        }

        // Check conflicts in lecture changes
        val changeConflict = lectureChanges.any { change ->
            change.remainingWeeks > 0 &&
                    change.newClassroom.room.id == newRoomId &&
                    change.newWeekDay == newWeekDay &&
                    (currentLecture == null || change.lecture.id != currentLecture.id) &&
                    (timeOverlap(
                        newStartTime, newEndTime,
                        change.newStartTime, change.newEndTime
                    ))
        }

        return lectureConflict || changeConflict
    }

    private fun timeOverlap(
        start1: Duration, end1: Duration,
        start2: Duration, end2: Duration
    ): Boolean {
        return (start1 >= start2 && start1 < end2) || // Starts during
                (end1 > start2 && end1 <= end2) ||    // Ends during
                (start1 <= start2 && end1 >= end2)     // Completely contains
    }

    private fun applyActiveChanges(lecture: Lecture): Lecture {
        val activeChange = lectureChanges.firstOrNull { it.lecture.id == lecture.id && it.remainingWeeks > 0 }
        return if (activeChange != null) {
            lecture.apply{
                weekDay = activeChange.newWeekDay
                startTime = activeChange.newStartTime
                endTime = activeChange.newEndTime
                classroom = activeChange.newClassroom
            }
        } else lecture
    }

    fun clear() {
        lectures.clear()
        lectureChanges.clear()
    }
}