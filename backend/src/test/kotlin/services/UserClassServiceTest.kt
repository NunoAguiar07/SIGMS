package services

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.entities.users.Student
import isel.leic.group25.db.entities.users.Student.Companion.invoke
import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.entities.users.Teacher.Companion.invoke
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.services.UserClassService
import isel.leic.group25.services.errors.UserClassError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import isel.leic.group25.utils.hoursAndMinutesToDuration
import mocks.repositories.MockRepositories
import mocks.repositories.rooms.MockRoomRepository
import mocks.repositories.timetables.MockClassRepository
import mocks.repositories.timetables.MockLectureRepository
import mocks.repositories.timetables.MockUniversityRepository
import mocks.repositories.users.MockStudentRepository
import mocks.repositories.users.MockTeacherRepository
import mocks.repositories.users.MockUserRepository
import mocks.repositories.utils.MockTransaction
import org.ktorm.database.Database
import kotlin.test.*

class UserClassServiceTest {
    // Mocks
    val mockDB = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        password = ""
    )
    private val mockRepositories = MockRepositories(mockDB)

    private val service = UserClassService(
        mockRepositories,
        mockRepositories.ktormCommand
    )

    // Helper functions
    private fun createTestUser(role: Role): User {
        val newUniversity = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val user = mockRepositories.from({userRepository}){
            createWithRole(
                email = "${role.name.lowercase()}@test.com",
                username = "test${role.name.lowercase()}",
                password = "password123",
                role = role,
                authProvider = "local",
                university = newUniversity
            )
        }
        when (role)  {
            Role.STUDENT -> mockRepositories.from({studentRepository as MockStudentRepository}){addMockStudent(user)}
            Role.TEACHER -> mockRepositories.from({teacherRepository as MockTeacherRepository}){addMockTeacher(user)}
            else -> fail("Unexpected role: $role")
        }
        return user
    }

    private fun createTestClass(): Class {
        return mockRepositories.from({classRepository}){
            addClass(
                name = "Test Class",
                subject = Subject {
                    this.name = "PG"
                }
            )
        }
    }

    @AfterTest
    fun cleanup() {
        mockRepositories.from({userRepository as MockUserRepository}){clear()}
        mockRepositories.from({studentRepository as MockStudentRepository}){clear()}
        mockRepositories.from({teacherRepository as MockTeacherRepository}){clear()}
        mockRepositories.from({classRepository as MockClassRepository}){clearAll()}
        mockRepositories.from({lectureRepository as MockLectureRepository}){clear()}
    }

    @Test
    fun `addUserToClass should succeed for valid student`() {
        val studentUser = createTestUser(Role.STUDENT)
        val testClass = createTestClass()
        val result = service.addUserToClass(studentUser.id, testClass.id, Role.STUDENT)
        assertTrue(result is Success)
        assertTrue(result.value)
        assertTrue(mockRepositories.from({classRepository}){
            checkStudentInClass(studentUser.id, testClass.id)
        })
    }

    @Test
    fun `addUserToClass should succeed for valid teacher`() {
        val teacherUser = createTestUser(Role.TEACHER)
        val testClass = createTestClass()

        val result = service.addUserToClass(teacherUser.id, testClass.id, Role.TEACHER)

        assertTrue(result is Success)
        assertTrue(result.value)
        assertTrue(mockRepositories.from({classRepository}){
            checkTeacherInClass(teacherUser.id, testClass.id)
        })
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
        assertFalse(mockRepositories.from({classRepository}){
            checkStudentInClass(studentUser.id, testClass.id)
        })
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
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val room = mockRepositories.from({roomRepository}){
            createRoom(20, "2.01", university)
        }
        mockRepositories.from({roomRepository}){
            createClassRoom(room)
        }
        val classroom = mockRepositories.from({roomRepository}){
            getClassRoomById(room.id)
        }
        assertNotNull(classroom)
        mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = testClass,
                type = ClassType.PRACTICAL,
                classroom = classroom,
                weekDay = WeekDay.MONDAY,
                startTime = "10:00".hoursAndMinutesToDuration(),
                endTime = "11:00".hoursAndMinutesToDuration(),
            )
        }
        mockRepositories.from({lectureRepository}){
            createLecture(
                schoolClass = testClass,
                type = ClassType.PRACTICAL,
                classroom = classroom,
                weekDay = WeekDay.MONDAY,
                startTime = "15:00".hoursAndMinutesToDuration(),
                endTime = "17:00".hoursAndMinutesToDuration(),
            )
        }

        // Add student to class
        service.addUserToClass(studentUser.id, testClass.id, Role.STUDENT)

        val result = service.getScheduleByUserId(studentUser.id, Role.STUDENT, 10, 0)

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

        val result = service.getScheduleByUserId(studentUser.id, Role.STUDENT, 10, 0)

        assertTrue(result is Success)
        assertTrue(result.value.isEmpty())
    }

}