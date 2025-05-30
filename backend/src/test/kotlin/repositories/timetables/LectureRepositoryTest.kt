package repositories.timetables

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.timetables.LectureRepository
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import repositories.DatabaseTestSetup
import kotlin.test.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
class LectureRepositoryTemporalTest {
    private val kTormCommand = KtormCommand(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)
    private val lectureRepository = LectureRepository(DatabaseTestSetup.database)
    private val classRepository = ClassRepository(DatabaseTestSetup.database)
    private val roomRepository = RoomRepository(DatabaseTestSetup.database)
    private val subjectRepository = SubjectRepository(DatabaseTestSetup.database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    private fun createTestLecture(): Lecture = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Test Subject", university)
        val clazz = classRepository.addClass("Test Class", subject)
        val room = roomRepository.createRoom(20, "Test Room", university)
        roomRepository.createClassRoom(room)
        val classroom = roomRepository.getClassRoomById(room.id)!!

        lectureRepository.createLecture(
            schoolClass = clazz,
            classroom = classroom,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.MONDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )
    }

    @Test
    fun `Should apply permanent change when both dates are null`() = kTormCommand.useTransaction {
        val lecture = createTestLecture()
        val newRoom = roomRepository.createRoom(30, "New Room", lecture.classroom.room.university).let {
            roomRepository.createClassRoom(it)
            roomRepository.getClassRoomById(it.id)!!
        }

        val updatedLecture = lectureRepository.updateLecture(
            lecture = lecture,
            newClassroom = newRoom,
            newType = ClassType.PRACTICAL,
            newWeekDay = WeekDay.TUESDAY,
            newStartTime = 10.hours,
            newEndTime = 12.hours,
            effectiveFrom = null,
            effectiveUntil = null
        )

        assertEquals(newRoom, updatedLecture.classroom)
        assertEquals(ClassType.PRACTICAL, updatedLecture.type)
        assertEquals(WeekDay.TUESDAY, updatedLecture.weekDay)
        assertEquals(10.hours, updatedLecture.startTime)
        assertEquals(12.hours, updatedLecture.endTime)

        // Verify no change record was created
        //assertFalse(lectureRepository.hasActiveChanges(lecture.id))
    }

    @Test
    fun `Should apply immediate change when start date is null`() = kTormCommand.useTransaction {
        val lecture = createTestLecture()
        val newRoom = roomRepository.createRoom(30, "New Room", lecture.classroom.room.university).let {
            roomRepository.createClassRoom(it)
            roomRepository.getClassRoomById(it.id)!!
        }
        val endDate = Clock.System.now().plus(7.days)

        val updatedLecture = lectureRepository.updateLecture(
            lecture = lecture,
            newClassroom = newRoom,
            newType = ClassType.PRACTICAL,
            newWeekDay = WeekDay.TUESDAY,
            newStartTime = 10.hours,
            newEndTime = 12.hours,
            effectiveFrom = null,
            effectiveUntil = endDate
        )

        // Should immediately apply changes
        assertEquals(newRoom, updatedLecture.classroom)
        assertEquals(ClassType.PRACTICAL, updatedLecture.type)
        assertEquals(WeekDay.TUESDAY, updatedLecture.weekDay)
        assertEquals(10.hours, updatedLecture.startTime)
        assertEquals(12.hours, updatedLecture.endTime)

        // Verify change record was created
        //assertTrue(lectureRepository.hasActiveChanges(lecture.id))
    }

    @Test
    fun `Should schedule future change when both dates are provided`() = kTormCommand.useTransaction {
        val lecture = createTestLecture()
        val newRoom = roomRepository.createRoom(30, "New Room", lecture.classroom.room.university).let {
            roomRepository.createClassRoom(it)
            roomRepository.getClassRoomById(it.id)!!
        }
        val startDate = Clock.System.now().plus(1.days)
        val endDate = startDate.plus(7.days)

        lectureRepository.updateLecture(
            lecture = lecture,
            newClassroom = newRoom,
            newType = ClassType.PRACTICAL,
            newWeekDay = WeekDay.TUESDAY,
            newStartTime = 10.hours,
            newEndTime = 12.hours,
            effectiveFrom = startDate,
            effectiveUntil = endDate
        )

        val lectureChangeStored = lectureRepository.getLectureChangeById(lecture.id)

        assertNotNull(lectureChangeStored)

        // Should not immediately apply changes
        assertEquals(newRoom, lectureChangeStored.originalClassroom)
        assertEquals(ClassType.PRACTICAL, lectureChangeStored.originalType)
        assertEquals(WeekDay.TUESDAY, lectureChangeStored.originalWeekDay)
        assertEquals(10.hours, lectureChangeStored.originalStartTime)
        assertEquals(12.hours, lectureChangeStored.originalEndTime)
    }

    @Test
    fun `Should detect conflicts with permanent changes`() = kTormCommand.useTransaction {
        val lecture = createTestLecture()
        val conflictingRoom = roomRepository.createRoom(30, "Conflicting Room", lecture.classroom.room.university).let {
            roomRepository.createClassRoom(it)
            roomRepository.getClassRoomById(it.id)!!
        }

        // Create a conflicting lecture
        lectureRepository.createLecture(
            schoolClass = lecture.schoolClass,
            classroom = conflictingRoom,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.MONDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )

        val hasConflict = lectureRepository.findConflictingLectures(
            newRoomId = conflictingRoom.room.id,
            newWeekDay = WeekDay.MONDAY,
            newStartTime = 9.hours,
            newEndTime = 11.hours,
            effectiveFrom = null,
            effectiveUntil = null,
            currentLecture = null
        )

        assertTrue(hasConflict)
    }

    @Test
    fun `Should detect conflicts with immediate changes`() = kTormCommand.useTransaction {
        val lecture = createTestLecture()
        val conflictingRoom = roomRepository.createRoom(30, "Conflicting Room", lecture.classroom.room.university).let {
            roomRepository.createClassRoom(it)
            roomRepository.getClassRoomById(it.id)
        }
        assertNotNull(conflictingRoom)
        val endDate = Clock.System.now().plus(7.days)

        // Create a conflicting future change
        lectureRepository.updateLecture(
            lecture = lecture,
            newClassroom = conflictingRoom,
            newType = ClassType.PRACTICAL,
            newWeekDay = WeekDay.TUESDAY,
            newStartTime = 10.hours,
            newEndTime = 12.hours,
            effectiveFrom = Clock.System.now().plus(1.days),
            effectiveUntil = endDate.plus(7.days)
        )

        val hasConflict = lectureRepository.findConflictingLectures(
            newRoomId = conflictingRoom.room.id,
            newWeekDay = WeekDay.TUESDAY,
            newStartTime = 10.hours,
            newEndTime = 12.hours,
            effectiveFrom = null,
            effectiveUntil = endDate,
            currentLecture = null
        )

        assertTrue(hasConflict)
    }

    @Test
    fun `Should detect conflicts with scheduled changes`() = kTormCommand.useTransaction {
        val lecture = createTestLecture()
        val conflictingRoom = roomRepository.createRoom(30, "Conflicting Room", lecture.classroom.room.university).let {
            roomRepository.createClassRoom(it)
            roomRepository.getClassRoomById(it.id)!!
        }
        val startDate = Clock.System.now().plus(1.days)
        val endDate = startDate.plus(7.days)

        // Create a conflicting scheduled change
        lectureRepository.updateLecture(
            lecture = lecture,
            newClassroom = conflictingRoom,
            newType = ClassType.PRACTICAL,
            newWeekDay = WeekDay.TUESDAY,
            newStartTime = 10.hours,
            newEndTime = 12.hours,
            effectiveFrom = startDate,
            effectiveUntil = endDate
        )

        val hasConflict = lectureRepository.findConflictingLectures(
            newRoomId = conflictingRoom.room.id,
            newWeekDay = WeekDay.TUESDAY,
            newStartTime = 10.hours,
            newEndTime = 12.hours,
            effectiveFrom = startDate.minus(1.days),
            effectiveUntil = endDate.plus(1.days),
            currentLecture = null
        )

        assertTrue(hasConflict)
    }

    @Test
    fun `Should delete lecture change and restore original values`() = kTormCommand.useTransaction {
        val lecture = createTestLecture()
        val storeLecture = lectureRepository.getLectureById(lecture.id)
        assertNotNull(storeLecture)
        val newRoom = roomRepository.createRoom(30, "New Room", lecture.classroom.room.university).let {
            roomRepository.createClassRoom(it)
            roomRepository.getClassRoomById(it.id)!!
        }
        val endDate = Clock.System.now().plus(7.days)

        // Create a change
        lectureRepository.updateLecture(
            lecture = lecture,
            newClassroom = newRoom,
            newType = ClassType.PRACTICAL,
            newWeekDay = WeekDay.TUESDAY,
            newStartTime = 10.hours,
            newEndTime = 12.hours,
            effectiveFrom = null,
            effectiveUntil = endDate
        )

        // Delete the change
        assertTrue(lectureRepository.deleteLectureChange(lecture.id))

        // Verify original values were restored
        val restoredLecture = lectureRepository.getLectureById(lecture.id)!!
        assertEquals(storeLecture.classroom, restoredLecture.classroom)
        assertEquals(storeLecture.type, restoredLecture.type)
        assertEquals(storeLecture.weekDay, restoredLecture.weekDay)
        assertEquals(storeLecture.startTime, restoredLecture.startTime)
        assertEquals(storeLecture.endTime, restoredLecture.endTime)
    }
}