package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.timetables.LectureChange
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import isel.leic.group25.db.repositories.utils.withDatabase
import isel.leic.group25.db.tables.Tables.Companion.lectureChanges
import isel.leic.group25.db.tables.Tables.Companion.lectures
import isel.leic.group25.db.tables.timetables.LectureChanges
import isel.leic.group25.db.tables.timetables.Lectures
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.ColumnDeclaring
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


class LectureRepository(private val database: Database) : LectureRepositoryInterface {
    override fun getLectureById(id: Int): Lecture? = withDatabase {
        return database.lectures.firstOrNull { it.id eq id }
    }

    override fun getLectureChangeById(id: Int): LectureChange? {
        return database.lectureChanges.firstOrNull { it.lectureId eq id }
    }

    override fun getAllLectures(): List<Lecture> = withDatabase {
        return database.lectures.toList()
    }

    override fun getAllLectures(limit: Int, offset: Int): List<Lecture> = withDatabase {
        return database.lectures.drop(offset).take(limit).toList()
    }

    override fun getAllLectureChanges(): List<LectureChange> = withDatabase {
        return database.lectureChanges.toList()
    }

    override fun getAllLectureChanges(limit: Int, offset: Int): List<LectureChange> = withDatabase {
        return database.lectureChanges.drop(offset).take(limit).toList()
    }

    override fun getLecture(
        schoolClass: Class,
        classroom: Classroom,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture? = withDatabase {
       return database.lectures.firstOrNull {
            (it.classId eq schoolClass.id) and
                    (it.roomId eq classroom.room.id) and
                    (it.type eq type) and
                    (it.weekDay eq weekDay) and
                    (it.startTime eq startTime) and
                    (it.endTime eq endTime)
        }
    }

    override fun createLecture(
        schoolClass: Class,
        classroom: Classroom,
        type: ClassType,
        weekDay: WeekDay,
        startTime: Duration,
        endTime: Duration
    ): Lecture = withDatabase {
        val newLecture = Lecture {
            this.schoolClass = schoolClass
            this.classroom = classroom
            this.type = type
            this.weekDay = weekDay
            this.startTime = startTime
            this.endTime = endTime
        }
        database.lectures.add(newLecture)
        return newLecture
    }

    override fun getLecturesByRoom(roomId: Int, limit:Int, offSet:Int): List<Lecture> = withDatabase {
        return database.lectures.filter { it.roomId eq roomId }.drop(offSet).take(limit).toList()
    }

    override fun getLecturesByClass(classId: Int, limit:Int, offSet:Int): List<Lecture> = withDatabase {
        return database.lectures.filter { it.classId eq classId }.drop(offSet).take(limit).toList()
    }

    override fun getLecturesByType(type: ClassType): List<Lecture> = withDatabase {
        return database.lectures.filter { it.type eq type }.map { it }
    }

    override fun deleteLecture(id: Int): Boolean = withDatabase {
        return database.lectures.removeIf { it.id eq id } > 0
    }

    override fun deleteLectureChange(id: Int): Boolean = withDatabase {
        val lectureChange = database.lectureChanges.firstOrNull { it.lectureId eq id }
        if (lectureChange != null) {
            // Restore original values from lecture_change to lecture
            val lecture = database.lectures.first { it.id eq id }
            lecture.apply {
                classroom = lectureChange.originalClassroom
                weekDay = lectureChange.originalWeekDay
                startTime = lectureChange.originalStartTime
                endTime = lectureChange.originalEndTime
                type = lectureChange.originalType
            }
            database.lectures.update(lecture)

            // Remove the change record
            return database.lectureChanges.removeIf { it.lectureId eq id } > 0
        }
        return false
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
    ): Lecture = withDatabase {
        return when {
            isPermanentChange(effectiveFrom, effectiveUntil) ->
                applyPermanentChange(lecture, newClassroom, newType, newWeekDay, newStartTime, newEndTime)
            isImmediateChange(effectiveFrom) ->
                applyImmediateChange(lecture, newClassroom, newType, newWeekDay, newStartTime, newEndTime, effectiveUntil)
            else ->{
                lecture.apply {
                    classroom = newClassroom
                    type = newType
                    weekDay = newWeekDay
                    startTime = newStartTime
                    endTime = newEndTime
                }
                scheduleFutureChange(lecture, effectiveFrom, effectiveUntil)
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
    ): Boolean = withDatabase {
        return when {
            // Case 1: Permanent change (check all lectures and all changes)
            isPermanentChange(effectiveFrom, effectiveUntil) ->
                hasPermanentChangeConflict(newRoomId, newWeekDay, newStartTime, newEndTime, currentLecture)

            // Case 2: Immediate change until end date (check current lectures and future changes)
            isImmediateChange(effectiveFrom) ->
                hasImmediateChangeConflict(newRoomId, newWeekDay, newStartTime, newEndTime, effectiveUntil!!, currentLecture)

            // Case 3: Scheduled change (check current lectures and overlapping changes)
            else ->
                hasScheduledChangeConflict(newRoomId, newWeekDay, newStartTime, newEndTime, effectiveFrom!!, effectiveUntil!!, currentLecture)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun isPermanentChange(effectiveFrom: Instant?, effectiveUntil: Instant?): Boolean {
        return effectiveFrom == null && effectiveUntil == null
    }

    @OptIn(ExperimentalTime::class)
    private fun isImmediateChange(effectiveFrom: Instant?): Boolean {
        return effectiveFrom == null
    }

    private fun applyPermanentChange(
        lecture: Lecture,
        newClassroom: Classroom,
        newType: ClassType,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration
    ): Lecture = withDatabase {
        lecture.apply {
            classroom = newClassroom
            type = newType
            weekDay = newWeekDay
            startTime = newStartTime
            endTime = newEndTime
        }
        database.lectures.update(lecture)
        return lecture
    }

    @OptIn(ExperimentalTime::class)
    private fun createLectureChangeRecord(
        lecture: Lecture,
        effectiveFrom: Instant?,
        effectiveUntil: Instant?
    ): LectureChange {
        return LectureChange {
            this.lecture = lecture
            this.originalClassroom = lecture.classroom
            this.originalWeekDay = lecture.weekDay
            this.originalStartTime = lecture.startTime
            this.originalEndTime = lecture.endTime
            this.originalType = lecture.type
            this.effectiveFrom = effectiveFrom
            this.effectiveUntil = effectiveUntil
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun applyImmediateChange(
        lecture: Lecture,
        newClassroom: Classroom,
        newType: ClassType,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        effectiveUntil: Instant?
    ): Lecture = withDatabase {
        val changeRecord = createLectureChangeRecord(lecture, null, effectiveUntil)
        database.lectureChanges.add(changeRecord)
        lecture.apply {
            classroom = newClassroom
            type = newType
            weekDay = newWeekDay
            startTime = newStartTime
            endTime = newEndTime
        }
        database.lectures.update(lecture)

        return lecture
    }

    @OptIn(ExperimentalTime::class)
    private fun scheduleFutureChange(
        lecture: Lecture,
        effectiveFrom: Instant?,
        effectiveUntil: Instant?
    ): Lecture = withDatabase {
        val changeRecord = createLectureChangeRecord(lecture, effectiveFrom, effectiveUntil)
        database.lectureChanges.add(changeRecord)
        return lecture
    }

    private fun hasPermanentChangeConflict(
        newRoomId: Int,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        currentLecture: Lecture?
    ): Boolean = withDatabase {
        // Check conflicts with existing lectures
        val lectureConflict = database.lectures.any { lecture ->
            (isDifferentLecture(lecture.id, currentLecture?.id ?: -1)) and
                    isSameRoomAndDay(lecture, newRoomId, newWeekDay) and
                    hasTimeOverlap(newStartTime, newEndTime, lecture.startTime, lecture.endTime)
        }

        // Check conflicts with all scheduled changes
        val changeConflict = database.lectureChanges.any { change ->
            isDifferentLecture(change.lectureId, currentLecture?.id ?: -1) and
                    isSameRoomAndDay(change, newRoomId, newWeekDay) and
                    hasTimeOverlap(newStartTime, newEndTime, change.originalStartTime, change.originalEndTime)
        }

        return lectureConflict || changeConflict
    }

    @OptIn(ExperimentalTime::class)
    private fun hasImmediateChangeConflict(
        newRoomId: Int,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        changeEndDate: Instant,
        currentLecture: Lecture?
    ): Boolean = withDatabase {
        // Check conflicts with existing lectures
        val lectureConflict = database.lectures.any { lecture ->
            isDifferentLecture(lecture.id, currentLecture?.id ?: -1) and
                    isSameRoomAndDay(lecture, newRoomId, newWeekDay) and
                    hasTimeOverlap(newStartTime, newEndTime, lecture.startTime, lecture.endTime)
        }

        // Check conflicts with changes that extend beyond our end date
        val changeConflict = database.lectureChanges.any { change ->
            isDifferentLecture(change.lectureId, currentLecture?.id ?: -1) and
                    isSameRoomAndDay(change, newRoomId, newWeekDay) and
                    ((change.effectiveTo.isNull()) or (change.effectiveTo greater changeEndDate)) and
                    hasTimeOverlap(newStartTime, newEndTime, change.originalStartTime, change.originalEndTime)
        }

        return lectureConflict || changeConflict
    }

    @OptIn(ExperimentalTime::class)
    private fun hasScheduledChangeConflict(
        newRoomId: Int,
        newWeekDay: WeekDay,
        newStartTime: Duration,
        newEndTime: Duration,
        changeStartDate: Instant,
        changeEndDate: Instant,
        currentLecture: Lecture?
    ): Boolean = withDatabase {
        // Check conflicts with existing lectures
        val lectureConflict = database.lectures.any { lecture ->
            isDifferentLecture(lecture.id, currentLecture?.id ?: -1) and
                    isSameRoomAndDay(lecture, newRoomId, newWeekDay) and
                    hasTimeOverlap(newStartTime, newEndTime, lecture.startTime, lecture.endTime)
        }

        // Check conflicts with overlapping scheduled changes
        val changeConflict = database.lectureChanges.any { change ->
            isDifferentLecture(change.lectureId, currentLecture?.id ?: -1) and
                    isSameRoomAndDay(change, newRoomId, newWeekDay) and
                    hasTimeOverlap(newStartTime, newEndTime, change.originalStartTime, change.originalEndTime) and
                    hasDateOverlap(changeStartDate, changeEndDate, change.effectiveFrom, change.effectiveTo)
        }

        return lectureConflict || changeConflict
    }

    // Helper functions
    private fun isDifferentLecture(lectureId: ColumnDeclaring<Int>, currentLectureId: Int): ColumnDeclaring<Boolean> {
        return (lectureId neq currentLectureId)
    }

    private fun isSameRoomAndDay(lecture: Lectures, roomId: Int, weekDay: WeekDay): ColumnDeclaring<Boolean> {
        return (lecture.roomId eq roomId) and (lecture.weekDay eq weekDay)
    }

    private fun isSameRoomAndDay(change: LectureChanges, roomId: Int, weekDay: WeekDay): ColumnDeclaring<Boolean> {
        return (change.originalRoomId eq roomId) and (change.originalWeekDay eq weekDay)
    }

    private fun hasTimeOverlap(
        newStart: Duration,
        newEnd: Duration,
        existingStart: ColumnDeclaring<Duration>,
        existingEnd: ColumnDeclaring<Duration>
    ): ColumnDeclaring<Boolean> {
        return ((newStart greaterEq existingStart) and (newStart less existingEnd)) or // New starts during existing
                ((newEnd greater  existingStart) and (newEnd lessEq  existingEnd)) or     // New ends during existing
                ((newStart lessEq  existingStart) and (newEnd greaterEq  existingEnd))     // New completely contains existing
    }

    @OptIn(ExperimentalTime::class)
    private fun hasDateOverlap(
        newStart: Instant,
        newEnd: Instant,
        existingStart: ColumnDeclaring<Instant>,
        existingEnd: ColumnDeclaring<Instant>
    ): ColumnDeclaring<Boolean>  {
        return ((newStart greaterEq existingStart) and (newStart less existingEnd)) or // New starts during existing
                ((newEnd greater  existingStart) and (newEnd lessEq  existingEnd)) or     // New ends during existing
                ((newStart lessEq  existingStart) and (newEnd greaterEq  existingEnd))     // New completely contains existing
    }

}

