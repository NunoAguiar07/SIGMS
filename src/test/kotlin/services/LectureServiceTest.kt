package services

import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.services.LectureService
import isel.leic.group25.services.errors.LectureError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.rooms.MockRoomRepository
import mocks.repositories.timetables.MockClassRepository
import mocks.repositories.timetables.MockLectureRepository
import mocks.repositories.timetables.MockSubjectRepository
import mocks.repositories.timetables.MockUniversityRepository
import mocks.repositories.utils.MockTransaction
import mocks.services.MockEmailService
import kotlin.test.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

class LectureServiceTest {
    private val lectureRepository = MockLectureRepository()
    private val subjectRepository = MockSubjectRepository()
    private val classRepository = MockClassRepository()
    private val roomRepository = MockRoomRepository()
    private val universityRepository = MockUniversityRepository()
    private val emailService = MockEmailService()
    private val transactionInterface = MockTransaction()

    private val lectureService = LectureService(
        lectureRepository = lectureRepository,
        transactionInterface = transactionInterface,
        classRepository = classRepository,
        roomRepository = roomRepository,
        emailService = emailService
    )

    @AfterTest
    fun clearDatabase() {
        lectureRepository.clear()
        roomRepository.clear()
    }

    @Test
    fun `getAllLectures should return list of lectures`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("PG", university)
        val schoolClass = classRepository.addClass("LEIC51D", subject)
        val room = roomRepository.createRoom(30, "G.2.01", university)
        roomRepository.createClassRoom(room)
        val classroom = roomRepository.getClassRoomById(room.id)
        assertNotNull(classroom, "Classroom should be created")

        lectureRepository.createLecture(
            schoolClass = schoolClass,
            classroom = classroom,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.MONDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )

        // Test
        val result = lectureService.getAllLectures(10, 0)
        assertTrue(result is Success, "Should return success")
        assertEquals(1, result.value.size, "Should return one lecture")
    }

    @Test
    fun `getLectureById should return lecture when found`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Math", university)
        val schoolClass = classRepository.addClass("Algebra", subject)
        val room = roomRepository.createRoom(30, "Room 101", university)
        roomRepository.createClassRoom(room)
        val classroom = roomRepository.getClassRoomById(room.id)
        assertNotNull(classroom, "Classroom should be created")

        val lecture = lectureRepository.createLecture(
            schoolClass = schoolClass,
            classroom = classroom,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.MONDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )

        // Test
        val result = lectureService.getLectureById(lecture.id)
        assertTrue(result is Success, "Should return success")
        assertEquals(lecture.id, result.value.id, "Should return correct lecture")
    }

    @Test
    fun `getLectureById should return LectureNotFound when not found`() {
        val result = lectureService.getLectureById(999)
        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.LectureNotFound, result.value, "Should return LectureNotFound error")
    }

    @Test
    fun `createLecture should create new lecture with valid data`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Math", university)
        val schoolClass = classRepository.addClass("Algebra", subject)
        val room = roomRepository.createRoom(30, "Room 101", university)
        roomRepository.createClassRoom(room)

        // Test
        val result = lectureService.createLecture(
            schoolClassId = schoolClass.id,
            roomId = room.id,
            weekDay = WeekDay.MONDAY,
            type = ClassType.THEORETICAL,
            startTime = "09:00",
            endTime = "11:00"
        )

        assertTrue(result is Success, "Should return success")
        assertEquals(WeekDay.MONDAY, result.value.weekDay, "Should create lecture with correct weekday")
        assertEquals(ClassType.THEORETICAL, result.value.type, "Should create lecture with correct type")
    }

    @Test
    fun `createLecture should return InvalidLectureDate when end time is before start time`() {
        val result = lectureService.createLecture(
            schoolClassId = 1,
            roomId = 1,
            weekDay = WeekDay.MONDAY,
            type = ClassType.THEORETICAL,
            startTime = "11:00",
            endTime = "09:00"
        )

        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.InvalidLectureDate, result.value, "Should return InvalidLectureDate error")
    }

    @Test
    fun `createLecture should return LectureTimeConflict when conflicting lecture exists`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Math", university)
        val schoolClass = classRepository.addClass("Algebra", subject)
        val room = roomRepository.createRoom(30, "Room 101", university)
        roomRepository.createClassRoom(room)

        // Create first lecture
        lectureService.createLecture(
            schoolClassId = schoolClass.id,
            roomId = room.id,
            weekDay = WeekDay.MONDAY,
            type = ClassType.THEORETICAL,
            startTime = "09:00",
            endTime = "11:00"
        )

        // Try to create conflicting lecture
        val result = lectureService.createLecture(
            schoolClassId = schoolClass.id,
            roomId = room.id,
            weekDay = WeekDay.MONDAY,
            type = ClassType.THEORETICAL,
            startTime = "10:00",
            endTime = "12:00"
        )

        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.LectureTimeConflict, result.value, "Should return LectureTimeConflict error")
    }

    @Test
    fun `createLecture should return InvalidLectureRoom when room not found`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Math", university)
        val schoolClass = classRepository.addClass("Algebra", subject)

        val result = lectureService.createLecture(
            schoolClassId = schoolClass.id,
            roomId = 999, // non-existent room
            weekDay = WeekDay.MONDAY,
            type = ClassType.THEORETICAL,
            startTime = "09:00",
            endTime = "11:00"
        )

        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.InvalidLectureRoom, result.value, "Should return InvalidLectureRoom error")
    }

    @Test
    fun `createLecture should return InvalidLectureClass when class not found`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val room = roomRepository.createRoom(30, "Room 101", university)
        roomRepository.createClassRoom(room)

        val result = lectureService.createLecture(
            schoolClassId = 999, // non-existent class
            roomId = room.id,
            weekDay = WeekDay.MONDAY,
            type = ClassType.THEORETICAL,
            startTime = "09:00",
            endTime = "11:00"
        )

        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.InvalidLectureClass, result.value, "Should return InvalidLectureClass error")
    }

    @Test
    fun `getLecturesByRoom should return lectures for given room`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Math", university)
        val schoolClass = classRepository.addClass("Algebra", subject)
        val room1 = roomRepository.createRoom(30, "Room 101", university)
        val room2 = roomRepository.createRoom(30, "Room 102", university)
        roomRepository.createClassRoom(room1)
        roomRepository.createClassRoom(room2)
        val classroom1 = roomRepository.getClassRoomById(room1.id)
        val classroom2 = roomRepository.getClassRoomById(room2.id)
        assertNotNull(classroom1, "Classroom should be created")
        assertNotNull(classroom2, "Classroom should be created")

        // Create lectures in different rooms
        lectureRepository.createLecture(
            schoolClass = schoolClass,
            classroom = classroom1,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.MONDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )
        lectureRepository.createLecture(
            schoolClass = schoolClass,
            classroom = classroom2,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.TUESDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )

        // Test
        val result = lectureService.getLecturesByRoom(room1.id, 10, 0)
        assertTrue(result is Success, "Should return success")
        assertEquals(2, result.value.size, "Should return one lecture")
    }

    @Test
    fun `getLecturesByClass should return lectures for given class`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Math", university)
        val schoolClass1 = classRepository.addClass("Algebra", subject)
        val schoolClass2 = classRepository.addClass("Geometry", subject)
        val room = roomRepository.createRoom(30, "Room 101", university)
        roomRepository.createClassRoom(room)
        val classroom = roomRepository.getClassRoomById(room.id)
        assertNotNull(classroom, "Classroom should be created")

        // Create lectures for different classes
        lectureRepository.createLecture(
            schoolClass = schoolClass1,
            classroom = classroom,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.MONDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )
        lectureRepository.createLecture(
            schoolClass = schoolClass2,
            classroom = classroom,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.TUESDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )

        // Test
        val result = lectureService.getLecturesByClass(schoolClass1.id, 10, 0)
        assertTrue(result is Success, "Should return success")
        assertEquals(2, result.value.size, "Should return one lecture")
    }

    @Test
    fun `getLecturesByClass should return InvalidLectureClass when class not found`() {
        val result = lectureService.getLecturesByClass(999, 10, 0)
        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.InvalidLectureClass, result.value, "Should return InvalidLectureClass error")
    }

    @Test
    fun `getLecturesByType should return lectures of given type`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Math", university)
        val schoolClass = classRepository.addClass("Algebra", subject)
        val room = roomRepository.createRoom(30, "Room 101", university)
        roomRepository.createClassRoom(room)
        val classroom = roomRepository.getClassRoomById(room.id)
        assertNotNull(classroom)

        // Create lectures of different types
        lectureRepository.createLecture(
            schoolClass = schoolClass,
            classroom = classroom,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.MONDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )
        lectureRepository.createLecture(
            schoolClass = schoolClass,
            classroom = classroom,
            type = ClassType.PRACTICAL,
            weekDay = WeekDay.TUESDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )

        // Test
        val result = lectureService.getLecturesByType(ClassType.THEORETICAL)
        assertTrue(result is Success, "Should return success")
        assertEquals(1, result.value.size, "Should return one lecture")
        assertEquals(ClassType.THEORETICAL, result.value[0].type, "Should return lecture of correct type")
    }

    @Test
    fun `deleteLecture should return true when lecture is deleted`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Math", university)
        val schoolClass = classRepository.addClass("Algebra", subject)
        val room = roomRepository.createRoom(30, "Room 101",    university)
        roomRepository.createClassRoom(room)
        val classroom = roomRepository.getClassRoomById(room.id)
        assertNotNull(classroom)

        val lecture = lectureRepository.createLecture(
            schoolClass = schoolClass,
            classroom = classroom,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.MONDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )

        // Test
        val result = lectureService.deleteLecture(lecture.id)
        assertTrue(result is Success, "Should return success")
        assertTrue(result.value, "Should return true when lecture is deleted")
        assertNull(lectureRepository.getLectureById(lecture.id), "Lecture should be deleted")
    }

    @Test
    fun `deleteLecture should return LectureNotFound when lecture not found`() {
        val result = lectureService.deleteLecture(999)
        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.LectureNotFound, result.value, "Should return LectureNotFound error")
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `updateLecture should update lecture permanently when numberOfWeeks is 0`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Math", university)
        val schoolClass = classRepository.addClass("Algebra", subject)
        val room1 = roomRepository.createRoom(30, "Room 101", university)
        val room2 = roomRepository.createRoom(30, "Room 102", university)
        roomRepository.createClassRoom(room1)
        roomRepository.createClassRoom(room2)
        val classroom1 = roomRepository.getClassRoomById(room1.id)
        assertNotNull(classroom1)
        val classroom2 = roomRepository.getClassRoomById(room2.id)
        assertNotNull(classroom2)

        val lecture = lectureRepository.createLecture(
            schoolClass = schoolClass,
            classroom = classroom1,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.MONDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )

        // Test
        val result = lectureService.updateLecture(
            lectureId = lecture.id,
            newRoomId = room2.id,
            newType = ClassType.PRACTICAL,
            newWeekDay = WeekDay.TUESDAY,
            newStartTime = "10:00",
            newEndTime = "12:00",
            effectiveFrom = null,
            effectiveUntil = null,
        )

        assertTrue(result is Success, "Should return success")
        assertEquals(room2.id, result.value.classroom.room.id, "Should update room")
        assertEquals(ClassType.PRACTICAL, result.value.type, "Should update type")
        assertEquals(WeekDay.TUESDAY, result.value.weekDay, "Should update weekday")
        assertEquals(10.hours, result.value.startTime, "Should update start time")
        assertEquals(12.hours, result.value.endTime, "Should update end time")
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `updateLecture should create temporary change when numberOfWeeks greater than 0`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Math", university)
        val schoolClass = classRepository.addClass("Algebra", subject)
        val room1 = roomRepository.createRoom(30, "Room 101", university)
        val room2 = roomRepository.createRoom(30, "Room 102", university)
        roomRepository.createClassRoom(room1)
        roomRepository.createClassRoom(room2)
        val classroom1 = roomRepository.getClassRoomById(room1.id)
        assertNotNull(classroom1)
        val classroom2 = roomRepository.getClassRoomById(room2.id)
        assertNotNull(classroom2)

        val lecture = lectureRepository.createLecture(
            schoolClass = schoolClass,
            classroom = classroom1,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.MONDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )

        // Test
        val result = lectureService.updateLecture(
            lectureId = lecture.id,
            newRoomId = room2.id,
            newType = ClassType.PRACTICAL,
            newWeekDay = WeekDay.TUESDAY,
            newStartTime = "10:00",
            newEndTime = "12:00",
            effectiveFrom = null,
            effectiveUntil = null,
        )

        assertTrue(result is Success, "Should return success")
        // Check that change was created
        val updatedLecture = lectureRepository.getLectureById(lecture.id)
        assertNotNull(updatedLecture, "Updated lecture should exist")
        assertEquals(room2.id, updatedLecture.classroom.room.id, "Updated room should be")
        assertEquals(ClassType.PRACTICAL, updatedLecture.type, "Updated type should be")
        assertEquals(WeekDay.TUESDAY, updatedLecture.weekDay, "Updated weekday should be")
        assertEquals(10.hours, updatedLecture.startTime, "Updated start time should be")
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `updateLecture should return LectureNotFound when lecture not found`() {
        val result = lectureService.updateLecture(
            lectureId = 999,
            newRoomId = 1,
            newType = ClassType.THEORETICAL,
            newWeekDay = WeekDay.MONDAY,
            newStartTime = "10:00",
            newEndTime = "12:00",
            effectiveFrom = null,
            effectiveUntil = null,
        )

        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.LectureNotFound, result.value, "Should return LectureNotFound error")
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `updateLecture should return InvalidLectureRoom when new room not found`() {
        // Setup test data
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Math", university)
        val schoolClass = classRepository.addClass("Algebra", subject)
        val room = roomRepository.createRoom(30, "Room 101", university)
        roomRepository.createClassRoom(room)
        val classroom = roomRepository.getClassRoomById(room.id)
        assertNotNull(classroom)

        val lecture = lectureRepository.createLecture(
            schoolClass = schoolClass,
            classroom = classroom,
            type = ClassType.THEORETICAL,
            weekDay = WeekDay.MONDAY,
            startTime = 9.hours,
            endTime = 11.hours
        )

        val result = lectureService.updateLecture(
            lectureId = lecture.id,
            newRoomId = 999, // non-existent room
            newType = ClassType.THEORETICAL,
            newWeekDay = WeekDay.MONDAY,
            newStartTime = "10:00",
            newEndTime = "12:00",
            effectiveFrom = null,
            effectiveUntil = null,
        )

        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.InvalidLectureRoom, result.value, "Should return InvalidLectureRoom error")
    }

}