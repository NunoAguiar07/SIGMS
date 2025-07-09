package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.users.AdminRepository
import isel.leic.group25.db.repositories.users.UserRepository
import repositories.DatabaseTestSetup
import kotlin.test.*

class AdminRepositoryTest {

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    private val adminRepository = AdminRepository(DatabaseTestSetup.database)
    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)
    private val kTormCommand = KtormCommand(DatabaseTestSetup.database)

    @Test
    fun `Should create a new admin and find it by id`(){
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
            val admin = adminRepository.findAdminById(newUser.id)
            assertNotNull(admin)
            assertEquals(newUser.id, admin.user.id)
        }
    }

    @Test
    fun `Should create a new admin and find it by email`(){
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
            val admin = adminRepository.findAdminByEmail(newUser.email)
            assertNotNull(admin)
            assertEquals(newUser.id, admin.user.id)
        }
    }

    @Test
    fun `Should verify user is admin`(){
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
            assertTrue(adminRepository.isAdmin(newUser))
        }
    }

    @Test
    fun `Should verify user is not admin`(){
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university, it.authProvider) }
            assertFalse(adminRepository.isAdmin(newUser))
        }
    }

    @Test
    fun `Should get all admin emails`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("testUniversity")

        val admin1 = userRepository.createWithRole(
            "admin1@test.com", "admin1", User.hashPassword("123"),
            Role.ADMIN, university, "local"
        )
        val admin2 = userRepository.createWithRole(
            "admin2@test.com", "admin2", User.hashPassword("123"),
            Role.ADMIN, university, "local"
        )
        val student = userRepository.createWithRole(
            "student@test.com", "student", User.hashPassword("123"),
            Role.STUDENT, university, "local"
        )

        val emails = adminRepository.getAllAdminEmails()

        assertEquals(2, emails.size)
        assertTrue("admin1@test.com" in emails)
        assertTrue("admin2@test.com" in emails)
        assertFalse("student@test.com" in emails)
    }

    @Test
    fun `Should return null when admin email does not exist`() = kTormCommand.useTransaction {
        val result = adminRepository.findAdminByEmail("doesnotexist@test.com")
        assertNull(result)
    }

    @Test
    fun `Should return null when admin id does not exist`() = kTormCommand.useTransaction {
        val result = adminRepository.findAdminById(999999)
        assertNull(result)
    }

}