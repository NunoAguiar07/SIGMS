package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.users.StudentRepository
import isel.leic.group25.db.repositories.users.UserRepository
import repositories.DatabaseTestSetup
import kotlin.test.*

class StudentRepositoryTest {

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    private val studentRepository = StudentRepository(DatabaseTestSetup.database)
    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)
    private val kTransaction = KTransaction(DatabaseTestSetup.database)

    @Test
    fun `Should create a new student and find it by id`(){
        kTransaction.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university) }
            val student = studentRepository.findStudentById(newUser.id)
            assertNotNull(student)
            assertEquals(newUser.id, student.user.id)
        }
    }

    @Test
    fun `Should create a new student and find it by email`(){
        kTransaction.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university) }
            val student = studentRepository.findStudentByEmail(newUser.email)
            assertNotNull(student)
            assertEquals(newUser.id, student.user.id)
        }
    }

    @Test
    fun `Should verify user is student`(){
        kTransaction.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university) }
            assertTrue(studentRepository.isStudent(newUser))
        }
    }

    @Test
    fun `Should verify user is not student`(){
        kTransaction.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.ADMIN, it.university) }
            assertFalse(studentRepository.isStudent(newUser))
        }
    }
}