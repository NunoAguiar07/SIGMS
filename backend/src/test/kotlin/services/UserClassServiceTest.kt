package services

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.entities.users.Student
import isel.leic.group25.db.entities.users.Teacher
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

    // --- Existing tests omitted for brevity ---

    @Test
    fun `removeUserFromClass should succeed for existing teacher`() {
        val teacherUser = createTestUser(Role.TEACHER)
        val testClass = createTestClass()

        // Add teacher first
        service.addUserToClass(teacherUser.id, testClass.id, Role.TEACHER)

        // Then remove
        val result = service.removeUserFromClass(teacherUser.id, testClass.id, Role.TEACHER)

        assertTrue(result is Success)
        assertTrue(result.value)
        assertFalse(mockRepositories.from({classRepository}){
            checkTeacherInClass(teacherUser.id, testClass.id)
        })
    }

    @Test
    fun `removeUserFromClass should fail for teacher not in class`() {
        val teacherUser = createTestUser(Role.TEACHER)
        val testClass = createTestClass()

        val result = service.removeUserFromClass(teacherUser.id, testClass.id, Role.TEACHER)

        assertTrue(result is Failure)
        assertEquals(UserClassError.UserNotInClass, result.value)
    }

    @Test
    fun `addUserToClass should fail for invalid role`() {
        val user = createTestUser(Role.STUDENT)
        val testClass = createTestClass()

        val result = service.addUserToClass(user.id, testClass.id, Role.ADMIN)  // Assuming ADMIN is invalid for addUserToClass

        assertTrue(result is Failure)
        assertEquals(UserClassError.InvalidRole, result.value)
    }

    @Test
    fun `removeUserFromClass should fail for invalid role`() {
        val user = createTestUser(Role.STUDENT)
        val testClass = createTestClass()

        val result = service.removeUserFromClass(user.id, testClass.id, Role.ADMIN)  // Assuming ADMIN invalid for removal

        assertTrue(result is Failure)
        assertEquals(UserClassError.InvalidRole, result.value)
    }

    @Test
    fun `getUserClasses returns classes for student and teacher`() {
        val studentUser = createTestUser(Role.STUDENT)
        val teacherUser = createTestUser(Role.TEACHER)
        val testClass = createTestClass()

        service.addUserToClass(studentUser.id, testClass.id, Role.STUDENT)
        service.addUserToClass(teacherUser.id, testClass.id, Role.TEACHER)

        val studentClassesResult = service.getUserClasses(studentUser.id, Role.STUDENT, 10, 0)
        assertTrue(studentClassesResult is Success)
        assertTrue(studentClassesResult.value.any { it.id == testClass.id })

        val teacherClassesResult = service.getUserClasses(teacherUser.id, Role.TEACHER, 10, 0)
        assertTrue(teacherClassesResult is Success)
        assertTrue(teacherClassesResult.value.any { it.id == testClass.id })
    }

    @Test
    fun `getScheduleByUserId returns lectures for student`() {
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

        assertEquals(testClass.id, result.value[0].first.schoolClass.id)
        assertEquals(classroom.room.id, result.value[0].first.classroom.room.id)

        assertEquals(testClass.id, result.value[1].first.schoolClass.id)
        assertEquals(classroom.room.id, result.value[1].first.classroom.room.id)
    }

    @Test
    fun `getScheduleByUserId returns empty list for student with no classes`() {
        val studentUser = createTestUser(Role.STUDENT)

        val result = service.getScheduleByUserId(studentUser.id, Role.STUDENT, 10, 0)

        assertTrue(result is Success)
        assertTrue(result.value.isEmpty())
    }

    @Test
    fun `getScheduleByUserId returns lectures for teacher`() {
        val teacherUser = createTestUser(Role.TEACHER)
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

        // Assign teacher to class
        service.addUserToClass(teacherUser.id, testClass.id, Role.TEACHER)

        val result = service.getScheduleByUserId(teacherUser.id, Role.TEACHER, 10, 0)

        assertTrue(result is Success)
        assertTrue(result.value.isNotEmpty())

        assertEquals(testClass.id, result.value[0].first.schoolClass.id)
        assertEquals(classroom.room.id, result.value[0].first.classroom.room.id)
    }

    @Test
    fun `getScheduleByUserId returns empty list for teacher with no classes`() {
        val teacherUser = createTestUser(Role.TEACHER)

        val result = service.getScheduleByUserId(teacherUser.id, Role.TEACHER, 10, 0)

        assertTrue(result is Success)
        assertTrue(result.value.isEmpty())
    }
}
