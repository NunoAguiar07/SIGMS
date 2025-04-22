package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KTransaction
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
    private val kTransaction = KTransaction(DatabaseTestSetup.database)

    @Test
    fun `Should create a new student and find it by id`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it, Role.STUDENT) }
            val student = studentRepository.findStudentById(newUser.id)
            assertNotNull(student)
            assertEquals(newUser.id, student.user.id)
        }
    }

    @Test
    fun `Should create a new student and find it by email`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it, Role.STUDENT) }
            val student = studentRepository.findStudentByEmail(newUser.email)
            assertNotNull(student)
            assertEquals(newUser.id, student.user.id)
        }
    }

    @Test
    fun `Should verify user is student`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it, Role.STUDENT) }
            assertTrue(studentRepository.isStudent(newUser))
        }
    }

    @Test
    fun `Should verify user is not student`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it, Role.ADMIN) }
            assertFalse(studentRepository.isStudent(newUser))
        }
    }
}