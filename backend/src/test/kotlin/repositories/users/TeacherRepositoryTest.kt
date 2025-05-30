package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.timetables.UniversityRepository
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
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)
    private val kTormCommand = KtormCommand(DatabaseTestSetup.database)

    @Test
    fun `Should create a new teacher and find it by id`(){
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.TEACHER, it.university, it.authProvider) }
            val teacher = teacherRepository.findTeacherById(newUser.id)
            assertNotNull(teacher)
            assertEquals(newUser.id, teacher.user.id)
        }
    }

    @Test
    fun `Should create a new teacher and find it by email`(){
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.TEACHER, it.university, it.authProvider) }
            val teacher = teacherRepository.findTeacherByEmail(newUser.email)
            assertNotNull(teacher)
            assertEquals(newUser.id, teacher.user.id)
        }
    }

    @Test
    fun `Should verify user is teacher`(){
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.TEACHER, it.university, it.authProvider) }
            assertTrue(teacherRepository.isTeacher(newUser))
        }
    }

    @Test
    fun `Should verify user is not teacher`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.ADMIN, it.university, it.authProvider) }
            assertFalse(teacherRepository.isTeacher(newUser))
        }
    }
}