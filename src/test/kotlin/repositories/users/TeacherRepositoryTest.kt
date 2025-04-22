package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.users.TeacherRepository
import isel.leic.group25.db.repositories.users.UserRepository
import repositories.DatabaseTestSetup
import kotlin.test.*

class TeacherRepositoryTest {

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    private val teacherRepository = TeacherRepository(DatabaseTestSetup.database)
    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val kTransaction = KTransaction(DatabaseTestSetup.database)

    @Test
    fun `Should create a new teacher and find it by id`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it, Role.TEACHER) }
            val teacher = teacherRepository.findTeacherById(newUser.id)
            assertNotNull(teacher)
            assertEquals(newUser.id, teacher.user.id)
        }
    }

    @Test
    fun `Should create a new teacher and find it by email`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it, Role.TEACHER) }
            val teacher = teacherRepository.findTeacherByEmail(newUser.email)
            assertNotNull(teacher)
            assertEquals(newUser.id, teacher.user.id)
        }
    }

    @Test
    fun `Should verify user is teacher`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it, Role.TEACHER) }
            assertTrue(teacherRepository.isTeacher(newUser))
        }
    }

    @Test
    fun `Should verify user is not teacher`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it, Role.ADMIN) }
            assertFalse(teacherRepository.isTeacher(newUser))
        }
    }
}