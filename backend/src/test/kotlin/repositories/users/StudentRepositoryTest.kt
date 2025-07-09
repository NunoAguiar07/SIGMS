package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.users.StudentRepository
import isel.leic.group25.db.repositories.users.UserRepository
import repositories.DatabaseTestSetup
import kotlin.test.*

class StudentRepositoryTest {

    private val studentRepository = StudentRepository(DatabaseTestSetup.database)
    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)
    private val kTormCommand = KtormCommand(DatabaseTestSetup.database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    private fun createUserWithRole(email: String, role: Role): User = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("testUniversity")
        val password = User.hashPassword("test")
        userRepository.createWithRole(email, "tester", password, role, university, "local")
    }

    @Test
    fun `Should create a new student and find it by id`() = kTormCommand.useTransaction {
        val newUser = createUserWithRole("student1@test.com", Role.STUDENT)
        val student = studentRepository.findStudentById(newUser.id)
        assertNotNull(student)
        assertEquals(newUser.id, student.user.id)
    }

    @Test
    fun `Should return null when finding student by non-existent id`() = kTormCommand.useTransaction {
        val student = studentRepository.findStudentById(-1)
        assertNull(student)
    }

    @Test
    fun `Should create a new student and find it by email`() = kTormCommand.useTransaction {
        val newUser = createUserWithRole("student2@test.com", Role.STUDENT)
        val student = studentRepository.findStudentByEmail(newUser.email)
        assertNotNull(student)
        assertEquals(newUser.id, student.user.id)
    }

    @Test
    fun `Should return null when finding student by non-existent email`() = kTormCommand.useTransaction {
        val student = studentRepository.findStudentByEmail("nonexistent@test.com")
        assertNull(student)
    }

    @Test
    fun `Should verify user is student`() = kTormCommand.useTransaction {
        val newUser = createUserWithRole("student3@test.com", Role.STUDENT)
        assertTrue(studentRepository.isStudent(newUser))
    }

    @Test
    fun `Should verify user is not student`() = kTormCommand.useTransaction {
        val newUser = createUserWithRole("admin1@test.com", Role.ADMIN)
        assertFalse(studentRepository.isStudent(newUser))
    }

    @Test
    fun `Should verify user is not student when user does not exist in students table`() = kTormCommand.useTransaction {
        val newUniversity = universityRepository.createUniversity("testUniversity")
        val user = User {
            email = "missingstudent@test.com"
            username = "tester"
            password = User.hashPassword("test")
            profileImage = byteArrayOf()
            authProvider = "local"
            university = newUniversity
        }
        val createdUser = userRepository.createWithRole(user.email, user.username, user.password, Role.TEACHER, user.university, user.authProvider)
        assertFalse(studentRepository.isStudent(createdUser))
    }
}
