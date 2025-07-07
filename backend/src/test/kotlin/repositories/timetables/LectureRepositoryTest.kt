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
class LectureRepositoryTest {
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

        assertEquals(newRoom, updatedLecture.classroom)
        assertEquals(ClassType.PRACTICAL, updatedLecture.type)
        assertEquals(WeekDay.TUESDAY, updatedLecture.weekDay)
        assertEquals(10.hours, updatedLecture.startTime)
        assertEquals(12.hours, updatedLecture.endTime)
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
        assertEquals(newRoom, lectureChangeStored.originalClassroom)
        assertEquals(ClassType.PRACTICAL, lectureChangeStored.originalType)
        assertEquals(WeekDay.TUESDAY, lectureChangeStored.originalWeekDay)
        assertEquals(10.hours, lectureChangeStored.originalStartTime)
        assertEquals(12.hours, lectureChangeStored.originalEndTime)
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

        assertTrue(lectureRepository.deleteLectureChange(lecture.id))

        val restoredLecture = lectureRepository.getLectureById(lecture.id)!!
        assertEquals(storeLecture.classroom, restoredLecture.classroom)
        assertEquals(storeLecture.type, restoredLecture.type)
        assertEquals(storeLecture.weekDay, restoredLecture.weekDay)
        assertEquals(storeLecture.startTime, restoredLecture.startTime)
        assertEquals(storeLecture.endTime, restoredLecture.endTime)
    }

    @Test
    fun `Should return false when deleting non-existing lecture change`() = kTormCommand.useTransaction {
        val lecture = createTestLecture()
        val result = lectureRepository.deleteLectureChange(lecture.id)
        assertFalse(result)
    }

    @Test
    fun `Should not detect conflict with itself`() = kTormCommand.useTransaction {
        val lecture = createTestLecture()

        val hasConflict = lectureRepository.findConflictingLectures(
            newRoomId = lecture.classroom.room.id,
            newWeekDay = lecture.weekDay,
            newStartTime = lecture.startTime,
            newEndTime = lecture.endTime,
            effectiveFrom = null,
            effectiveUntil = null,
            currentLecture = lecture
        )

        assertFalse(hasConflict)
    }

    @Test
    fun `Should detect conflicts with overlapping scheduled changes`() = kTormCommand.useTransaction {
        val lecture = createTestLecture()
        val room = roomRepository.createRoom(50, "Partial Conflict Room", lecture.classroom.room.university).let {
            roomRepository.createClassRoom(it)
            roomRepository.getClassRoomById(it.id)!!
        }

        val originalStart = Clock.System.now().plus(5.days)
        val originalEnd = originalStart.plus(5.days)

        lectureRepository.updateLecture(
            lecture,
            room,
            ClassType.THEORETICAL,
            WeekDay.THURSDAY,
            14.hours,
            16.hours,
            effectiveFrom = originalStart,
            effectiveUntil = originalEnd
        )

        val overlapStart = originalStart.plus(3.days)
        val overlapEnd = overlapStart.plus(4.days)

        val hasConflict = lectureRepository.findConflictingLectures(
            newRoomId = room.room.id,
            newWeekDay = WeekDay.THURSDAY,
            newStartTime = 14.hours,
            newEndTime = 16.hours,
            effectiveFrom = overlapStart,
            effectiveUntil = overlapEnd,
            currentLecture = null
        )

        assertTrue(hasConflict)
    }
}