package services

import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.services.LectureService
import isel.leic.group25.services.errors.LectureError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.MockRepositories
import mocks.services.MockEmailService
import org.ktorm.database.Database
import kotlin.test.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

class LectureServiceTest {
    private val mockDB = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        password = ""
    )
    private val mockRepositories = MockRepositories(mockDB)
    private val emailService = MockEmailService()

    private val lectureService = LectureService(
        mockRepositories,
        mockRepositories.ktormCommand,
        emailService
    )

    @Test
    fun `getAllLectures should return list of lectures`() {
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("PG", university)
        }
        val schoolClass = mockRepositories.from({classRepository}){
            addClass("LEIC51D", subject)
        }
        val room = mockRepositories.from({roomRepository}){
            createRoom(30, "G.2.01", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room)}
        val classroom = mockRepositories.from({roomRepository}){getClassRoomById(room.id)}
        assertNotNull(classroom, "Classroom should be created")

        mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }

        val result = lectureService.getAllLectures(10, 0)
        assertTrue(result is Success, "Should return success")
        assertEquals(1, result.value.size, "Should return one lecture")
    }

    @Test
    fun `getLectureById should return lecture when found`() {
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}){
            addClass("Algebra", subject)
        }
        val room =mockRepositories.from({roomRepository}){
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room)}
        val classroom = mockRepositories.from({roomRepository}){getClassRoomById(room.id)}
        assertNotNull(classroom, "Classroom should be created")

        val lecture = mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }

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
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}){
            addClass("Algebra", subject)
        }
        val room =mockRepositories.from({roomRepository}){
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room)}
        val classroom = mockRepositories.from({roomRepository}){getClassRoomById(room.id)}
        assertNotNull(classroom, "Classroom should be created")

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
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}){
            addClass("Algebra", subject)
        }
        val room =mockRepositories.from({roomRepository}){
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room)}
        val classroom = mockRepositories.from({roomRepository}){getClassRoomById(room.id)}
        assertNotNull(classroom, "Classroom should be created")

        mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }

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
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}){
            addClass("Algebra", subject)
        }

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
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val room = mockRepositories.from({roomRepository}){
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room)}

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
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}){
            addClass("Algebra", subject)
        }
        val room1 =mockRepositories.from({roomRepository}){
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room1)}
        val room2 = mockRepositories.from({roomRepository}){
            createRoom(30, "Room 102", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room2)}
        val classroom1 = mockRepositories.from({roomRepository}){getClassRoomById(room1.id)}
        val classroom2 = mockRepositories.from({roomRepository}){getClassRoomById(room2.id)}
        assertNotNull(classroom1, "Classroom should be created")
        assertNotNull(classroom2, "Classroom should be created")

        mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom1,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }
        mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom2,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.TUESDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }
        val result = lectureService.getLecturesByRoom(room1.id, 10, 0)
        assertTrue(result is Success, "Should return success")
        assertEquals(1, result.value.size, "Should return one lecture")
    }

    @Test
    fun `getLecturesByClass should return lectures for given class`() {
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("Math", university)
        }
        val schoolClass1 = mockRepositories.from({classRepository}){
            addClass("Algebra", subject)
        }
        val schoolClass2 = mockRepositories.from({classRepository}){
            addClass("Geometry", subject)
        }
        val room = mockRepositories.from({roomRepository}){
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}){
            createClassRoom(room)
        }
        val classroom = mockRepositories.from({roomRepository}){
            getClassRoomById(room.id)
        }
        assertNotNull(classroom, "Classroom should be created")

        mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass1,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }
        mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass2,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.TUESDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }

        val result = lectureService.getLecturesByClass(schoolClass1.id, 10, 0)
        assertTrue(result is Success, "Should return success")
        assertEquals(1, result.value.size, "Should return one lecture")
    }

    @Test
    fun `getLecturesByClass should return InvalidLectureClass when class not found`() {
        val result = lectureService.getLecturesByClass(999, 10, 0)
        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.InvalidLectureClass, result.value, "Should return InvalidLectureClass error")
    }

    @Test
    fun `getLecturesByType should return lectures of given type`() {
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}){
            addClass("Algebra", subject)
        }
        val room =mockRepositories.from({roomRepository}){
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room)}
        val classroom = mockRepositories.from({roomRepository}){getClassRoomById(room.id)}
        assertNotNull(classroom, "Classroom should be created")

        mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }
        mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom,
                type = ClassType.PRACTICAL,
                weekDay = WeekDay.TUESDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }

        val result = lectureService.getLecturesByType(ClassType.THEORETICAL)
        assertTrue(result is Success, "Should return success")
        assertEquals(1, result.value.size, "Should return one lecture")
        assertEquals(ClassType.THEORETICAL, result.value[0].type, "Should return lecture of correct type")
    }

    @Test
    fun `deleteLecture should return true when lecture is deleted`() {
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}){
            addClass("Algebra", subject)
        }
        val room =mockRepositories.from({roomRepository}){
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room)}
        val classroom = mockRepositories.from({roomRepository}){getClassRoomById(room.id)}
        assertNotNull(classroom, "Classroom should be created")

        val lecture = mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }

        val result = lectureService.deleteLecture(lecture.id)
        assertTrue(result is Success, "Should return success")
        assertTrue(result.value, "Should return true when lecture is deleted")
        assertNull(mockRepositories.from({lectureRepository}){getLectureById(lecture.id)}, "Lecture should be deleted")
    }

    @Test
    fun `deleteLecture should return LectureNotFound when lecture not found`() {
        val result = lectureService.deleteLecture(999)
        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.LectureNotFound, result.value, "Should return LectureNotFound error")
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `updateLecture should create permanent change when from and until are null`() {
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}){
            addClass("Algebra", subject)
        }
        val room1 =mockRepositories.from({roomRepository}){
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room1)}
        val room2 =mockRepositories.from({roomRepository}){
            createRoom(30, "Room 102", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room2)}
        val classroom1 = mockRepositories.from({roomRepository}){getClassRoomById(room1.id)}
        val classroom2 = mockRepositories.from({roomRepository}){getClassRoomById(room2.id)}
        assertNotNull(classroom1, "Classroom should be created")
        assertNotNull(classroom2, "Classroom should be created")


        val lecture =  mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom1,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }

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
        val updatedLecture = mockRepositories.from({lectureRepository}){
            getLectureById(lecture.id)
        }
        assertNotNull(updatedLecture, "Updated lecture should exist")
        assertEquals(room2.id, updatedLecture.classroom.room.id, "Updated room should be")
        assertEquals(ClassType.PRACTICAL, updatedLecture.type, "Updated type should be")
        assertEquals(WeekDay.TUESDAY, updatedLecture.weekDay, "Updated weekday should be")
        assertEquals(10.hours, updatedLecture.startTime, "Updated start time should be")
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `updateLecture should create temporary change when from is null`() {
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}){
            addClass("Algebra", subject)
        }
        val room1 =mockRepositories.from({roomRepository}){
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room1)}
        val room2 =mockRepositories.from({roomRepository}){
            createRoom(30, "Room 102", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room2)}
        val classroom1 = mockRepositories.from({roomRepository}){getClassRoomById(room1.id)}
        val classroom2 = mockRepositories.from({roomRepository}){getClassRoomById(room2.id)}
        assertNotNull(classroom1, "Classroom should be created")
        assertNotNull(classroom2, "Classroom should be created")


        val lecture =  mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom1,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }

        val currentDate = Clock.System.now().plus(1.hours)

        val result = lectureService.updateLecture(
            lectureId = lecture.id,
            newRoomId = room2.id,
            newType = ClassType.PRACTICAL,
            newWeekDay = WeekDay.TUESDAY,
            newStartTime = "10:00",
            newEndTime = "12:00",
            effectiveFrom = null,
            effectiveUntil = currentDate,
        )

        assertTrue(result is Success, "Should return success")
        val updatedLecture = mockRepositories.from({lectureRepository}){
            getLectureById(lecture.id)
        }

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
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}){
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}){
            addClass("Algebra", subject)
        }
        val room =mockRepositories.from({roomRepository}){
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}){createClassRoom(room)}
        val classroom = mockRepositories.from({roomRepository}){getClassRoomById(room.id)}
        assertNotNull(classroom, "Classroom should be created")

        val lecture = mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }

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

    @OptIn(ExperimentalTime::class)
    @Test
    fun `updateLecture should return InvalidLectureDate when start time is after end time`() {
        val university = mockRepositories.from({universityRepository}) {
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}) {
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}) {
            addClass("Algebra", subject)
        }
        val room = mockRepositories.from({roomRepository}) {
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}) { createClassRoom(room) }
        val classroom = mockRepositories.from({roomRepository}) { getClassRoomById(room.id) }
        val lecture = mockRepositories.from({lectureRepository}) {
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom!!,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }

        val result = lectureService.updateLecture(
            lectureId = lecture.id,
            newRoomId = room.id,
            newType = ClassType.THEORETICAL,
            newWeekDay = WeekDay.MONDAY,
            newStartTime = "12:00", // after endTime
            newEndTime = "10:00",
            effectiveFrom = null,
            effectiveUntil = null
        )

        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.InvalidLectureDate, result.value, "Should return InvalidLectureDate error")
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `updateLecture should return InvalidLectureUntilDate when effectiveUntil is in the past`() {
        val university = mockRepositories.from({universityRepository}) {
            createUniversity("Test University")
        }
        val subject = mockRepositories.from({subjectRepository}) {
            createSubject("Math", university)
        }
        val schoolClass = mockRepositories.from({classRepository}) {
            addClass("Algebra", subject)
        }
        val room = mockRepositories.from({roomRepository}) {
            createRoom(30, "Room 101", university)
        }
        mockRepositories.from({roomRepository}) { createClassRoom(room) }
        val classroom = mockRepositories.from({roomRepository}) { getClassRoomById(room.id) }
        val lecture = mockRepositories.from({lectureRepository}) {
            createLecture(
                schoolClass = schoolClass,
                classroom = classroom!!,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
        }

        val past = Clock.System.now().minus(1.hours)

        val result = lectureService.updateLecture(
            lectureId = lecture.id,
            newRoomId = room.id,
            newType = ClassType.THEORETICAL,
            newWeekDay = WeekDay.MONDAY,
            newStartTime = "10:00",
            newEndTime = "12:00",
            effectiveFrom = null,
            effectiveUntil = past
        )

        assertTrue(result is Failure, "Should return failure")
        assertEquals(LectureError.InvalidLectureUntilDate, result.value, "Should return InvalidLectureUntilDate error")
    }
}