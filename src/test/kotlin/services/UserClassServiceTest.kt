package services

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.services.UserClassService
import isel.leic.group25.services.errors.UserClassError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import isel.leic.group25.utils.hoursAndMinutesToDuration
import mocks.repositories.rooms.MockRoomRepository
import mocks.repositories.timetables.MockClassRepository
import mocks.repositories.timetables.MockLectureRepository
import mocks.repositories.timetables.MockUniversityRepository
import mocks.repositories.users.MockStudentRepository
import mocks.repositories.users.MockTeacherRepository
import mocks.repositories.users.MockUserRepository
import mocks.repositories.utils.MockTransaction
import kotlin.test.*

class UserClassServiceTest {
    // Mocks
    private val userRepository = MockUserRepository()
    private val studentRepository = MockStudentRepository()
    private val teacherRepository = MockTeacherRepository()
    private val classRepository = MockClassRepository()
    private val lectureRepository = MockLectureRepository()
    private val roomRepository = MockRoomRepository()
    private val universityRepository = MockUniversityRepository()
    private val transactionInterface = MockTransaction()

    private val service = UserClassService(
        userRepository,
        studentRepository,
        teacherRepository,
        classRepository,
        lectureRepository,
        transactionInterface
    )

    // Helper functions
    private fun createTestUser(role: Role): User {
        val newUniversity = universityRepository.createUniversity("Test University")
        val user = userRepository.createWithRole(
            email = "${role.name.lowercase()}@test.com",
            username = "test${role.name.lowercase()}",
            password = "password123",
            role = role,
            university = newUniversity
        )
        when (role)  {
            Role.STUDENT -> studentRepository.addMockStudent(user)
            Role.TEACHER -> teacherRepository.addMockTeacher(user)
            else -> fail("Unexpected role: $role")
        }
        return user
    }

    private fun createTestClass(): Class {
        return classRepository.addClass(
            name = "Test Class",
            subject = Subject {
                this.name = "PG"
            }
        )
    }

    @AfterTest
    fun cleanup() {
        userRepository.clear()
        studentRepository.clear()
        teacherRepository.clear()
        classRepository.clearAll()
        lectureRepository.clear()
    }

    @Test
    fun `addUserToClass should succeed for valid student`() {
        val studentUser = createTestUser(Role.STUDENT)
        val testClass = createTestClass()
        val result = service.addUserToClass(studentUser.id, testClass.id, Role.STUDENT)
        assertTrue(result is Success)
        assertTrue(result.value)
        assertTrue(classRepository.checkStudentInClass(studentUser.id, testClass.id))
    }

    @Test
    fun `addUserToClass should succeed for valid teacher`() {
        val teacherUser = createTestUser(Role.TEACHER)
        val testClass = createTestClass()

        val result = service.addUserToClass(teacherUser.id, testClass.id, Role.TEACHER)

        assertTrue(result is Success)
        assertTrue(result.value)
        assertTrue(classRepository.checkTeacherInClass(teacherUser.id, testClass.id))
    }

    @Test
    fun `addUserToClass should fail for non-existent class`() {
        val studentUser = createTestUser(Role.STUDENT)
        val result = service.addUserToClass(studentUser.id, 999, Role.STUDENT)
        assertTrue(result is Failure)
        assertEquals(UserClassError.ClassNotFound, result.value)
    }

    @Test
    fun `addUserToClass should fail for non-existent user`() {
        val testClass = createTestClass()
        val result = service.addUserToClass(9999, testClass.id, Role.STUDENT)
        assertTrue(result is Failure)
        assertEquals(UserClassError.UserNotFound, result.value)
    }

    @Test
    fun `addUserToClass should fail for duplicate student in class`() {
        val studentUser = createTestUser(Role.STUDENT)
        val testClass = createTestClass()

        // First addition should succeed
        val firstResult = service.addUserToClass(studentUser.id, testClass.id, Role.STUDENT)
        assertTrue(firstResult is Success)

        // Second addition should fail
        val secondResult = service.addUserToClass(studentUser.id, testClass.id, Role.STUDENT)
        assertTrue(secondResult is Failure)
        assertEquals(UserClassError.UserAlreadyInClass, secondResult.value)
    }

    @Test
    fun `removeUserFromClass should succeed for existing student`() {
        val studentUser = createTestUser(Role.STUDENT)
        val testClass = createTestClass()

        // Add student first
        service.addUserToClass(studentUser.id, testClass.id, Role.STUDENT)

        // Then remove
        val result = service.removeUserFromClass(studentUser.id, testClass.id, Role.STUDENT)

        assertTrue(result is Success)
        assertTrue(result.value)
        assertFalse(classRepository.checkStudentInClass(studentUser.id, testClass.id))
    }

    @Test
    fun `removeUserFromClass should fail for student not in class`() {
        val studentUser = createTestUser(Role.STUDENT)
        val testClass = createTestClass()

        val result = service.removeUserFromClass(studentUser.id, testClass.id, Role.STUDENT)

        assertTrue(result is Failure)
        assertEquals(UserClassError.UserNotInClass, result.value)
    }

    @Test
    fun `getScheduleByUserId should return lectures for student`() {
        val studentUser = createTestUser(Role.STUDENT)
        val testClass = createTestClass()
        val university = universityRepository.createUniversity("Test University")
        val room = roomRepository.createRoom(20, "2.01", university)
        roomRepository.createClassRoom(room)
        val classroom = roomRepository.getClassRoomById(room.id)
        assertNotNull(classroom)
        lectureRepository.createLecture(
            schoolClass = testClass,
            type = ClassType.PRACTICAL,
            classroom = classroom,
            weekDay = WeekDay.MONDAY,
            startTime = "10:00".hoursAndMinutesToDuration(),
            endTime = "11:00".hoursAndMinutesToDuration(),
        )
        lectureRepository.createLecture(
            schoolClass = testClass,
            type = ClassType.PRACTICAL,
            classroom = classroom,
            weekDay = WeekDay.MONDAY,
            startTime = "15:00".hoursAndMinutesToDuration(),
            endTime = "17:00".hoursAndMinutesToDuration(),
        )

        // Add student to class
        service.addUserToClass(studentUser.id, testClass.id, Role.STUDENT)

        val result = service.getScheduleByUserId(studentUser.id, Role.STUDENT)

        assertTrue(result is Success)
        assertEquals(2, result.value.size)

        assertNotNull(result.value.first())
        assertEquals(testClass, result.value.first().schoolClass)
        assertEquals(classroom, result.value.first().classroom)

        assertNotNull(result.value[1])
        assertEquals(testClass, result.value[1].schoolClass)
        assertEquals(classroom, result.value[1].classroom)

    }

    @Test
    fun `getScheduleByUserId should return empty list for student with no classes`() {
        val studentUser = createTestUser(Role.STUDENT)

        val result = service.getScheduleByUserId(studentUser.id, Role.STUDENT)

        assertTrue(result is Success)
        assertTrue(result.value.isEmpty())
    }

}