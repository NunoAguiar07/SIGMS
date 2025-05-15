package mocks.repositories.timetables

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.timetables.LectureChange
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class MockLectureRepository : LectureRepositoryInterface {
    private val lectures = mutableListOf<Lecture>()
    private val lectureChanges = mutableListOf<LectureChange>()

    override fun getLectureById(id: Int): Lecture? {
        return lectures.firstOrNull { it.id == id }?.let { applyActiveChanges(it) }
    }

    override fun getLectureChangeById(id: Int): LectureChange? {
        return lectureChanges.firstOrNull { it.lecture.id == id }
    }

    override fun getAllLectures(): List<Lecture> {
        return lectures.map { applyActiveChanges(it) }
    }

    override fun getAllLectures(limit: Int, offset: Int): List<Lecture> {
        return lectures.drop(offset).take(limit).map { applyActiveChanges(it) }
    }

    override fun getAllLectureChanges(): List<LectureChange> {
        return lectureChanges
    }

    override fun getAllLectureChanges(limit: Int, offset: Int): List<LectureChange> {
        return lectureChanges.drop(offset).take(limit)
    }

    override fun getLecture(
        schoolClass: Class,
        classroom: Classroom,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture? {
        return lectures.firstOrNull {
            it.schoolClass.id == schoolClass.id &&
                    it.classroom.room.id == classroom.room.id &&
                    it.type == type &&
                    it.weekDay == weekDay &&
                    it.startTime == startTime &&
                    it.endTime == endTime
        }?.let { applyActiveChanges(it) }
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

    @OptIn(ExperimentalTime::class)
    override fun deleteLectureChange(id: Int): Boolean {
        val change = lectureChanges.firstOrNull { it.lecture.id == id } ?: return false
        // Restore original values
        val lecture = lectures.first { it.id == id }
        lecture.apply {
            classroom = change.originalClassroom
            weekDay = change.originalWeekDay
            startTime = change.originalStartTime
            endTime = change.originalEndTime
            type = change.originalType
        }
        return lectureChanges.remove(change)
    }

    @OptIn(ExperimentalTime::class)
    override fun updateLecture(
        lecture: Lecture,
        newClassroom: Classroom,
        newType: ClassType,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        effectiveFrom: Instant?,
        effectiveUntil: Instant?
    ): Lecture {
        return when {
            // Permanent change
            effectiveFrom == null && effectiveUntil == null -> {
                lecture.apply {
                    classroom = newClassroom
                    type = newType
                    weekDay = newWeekDay
                    startTime = newStartTime
                    endTime = newEndTime
                }
                lectures.replaceAll { if (it.id == lecture.id) lecture else it }
                lecture
            }
            // Immediate change
            effectiveFrom == null -> {
                // Store current values as original
                lectureChanges.add(LectureChange {
                    this.lecture = lecture
                    this.originalClassroom = lecture.classroom
                    this.originalWeekDay = lecture.weekDay
                    this.originalStartTime = lecture.startTime
                    this.originalEndTime = lecture.endTime
                    this.originalType = lecture.type
                    this.effectiveFrom = null
                    this.effectiveUntil = effectiveUntil
                })
                // Apply new values
                lecture.apply {
                    classroom = newClassroom
                    type = newType
                    weekDay = newWeekDay
                    startTime = newStartTime
                    endTime = newEndTime
                }
                lecture
            }
            // Future change
            else -> {
                lectureChanges.add(LectureChange {
                    this.lecture = lecture
                    this.originalClassroom = lecture.classroom
                    this.originalWeekDay = lecture.weekDay
                    this.originalStartTime = lecture.startTime
                    this.originalEndTime = lecture.endTime
                    this.originalType = lecture.type
                    this.effectiveFrom = effectiveFrom
                    this.effectiveUntil = effectiveUntil
                })
                lecture
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    override fun findConflictingLectures(
        newRoomId: Int,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        effectiveFrom: Instant?,
        effectiveUntil: Instant?,
        currentLecture: Lecture?
    ): Boolean {
        // Check conflicts with existing lectures (current state)
        val lectureConflict = lectures.any { lecture ->
            lecture.classroom.room.id == newRoomId &&
                    lecture.weekDay == newWeekDay &&
                    (currentLecture == null || lecture.id != currentLecture.id) &&
                    timeOverlap(newStartTime, newEndTime, lecture.startTime, lecture.endTime)
        }

        // Check conflicts with scheduled changes
        val changeConflict = lectureChanges.any { change ->
            change.originalClassroom.room.id == newRoomId &&
                    change.originalWeekDay == newWeekDay &&
                    (currentLecture == null || change.lecture.id != currentLecture.id) &&
                    timeOverlap(newStartTime, newEndTime, change.originalStartTime, change.originalEndTime) &&
                    when {
                        // Permanent change conflicts with all changes
                        effectiveFrom == null && effectiveUntil == null -> true
                        // Immediate change conflicts with future changes
                        effectiveFrom == null ->
                            change.effectiveUntil == null || change.effectiveUntil!! > effectiveUntil!!
                        // Scheduled change conflicts with overlapping changes
                        else ->
                            (change.effectiveFrom == null || effectiveUntil == null || change.effectiveFrom!! < effectiveUntil) &&
                                    (change.effectiveUntil == null || change.effectiveUntil!! > effectiveFrom)
                    }
        }

        return lectureConflict || changeConflict
    }

    private fun timeOverlap(start1: Duration, end1: Duration, start2: Duration, end2: Duration): Boolean {
        return (start1 >= start2 && start1 < end2) ||
                (end1 > start2 && end1 <= end2) ||
                (start1 <= start2 && end1 >= end2)
    }

    @OptIn(ExperimentalTime::class)
    private fun applyActiveChanges(lecture: Lecture): Lecture {
        val activeChange = lectureChanges.firstOrNull { change ->
            change.lecture.id == lecture.id
        }

        return if (activeChange != null) {
            lecture.apply {
                weekDay = activeChange.originalWeekDay
                startTime = activeChange.originalStartTime
                endTime = activeChange.originalEndTime
                classroom = activeChange.originalClassroom
                type = activeChange.originalType
            }
        } else {
            lecture
        }
    }

    fun clear() {
        lectures.clear()
        lectureChanges.clear()
    }
}