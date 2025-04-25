package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KTransaction
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
    private val kTransaction = KTransaction(DatabaseTestSetup.database)

    @Test
    fun `Should create a new admin and find it by id`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.ADMIN) }
            val admin = adminRepository.findAdminById(newUser.id)
            assertNotNull(admin)
            assertEquals(newUser.id, admin.user.id)
        }
    }

    @Test
    fun `Should create a new admin and find it by email`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.ADMIN) }
            val admin = adminRepository.findAdminByEmail(newUser.email)
            assertNotNull(admin)
            assertEquals(newUser.id, admin.user.id)
        }
    }

    @Test
    fun `Should verify user is admin`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.ADMIN) }
            assertTrue(adminRepository.isAdmin(newUser))
        }
    }

    @Test
    fun `Should verify user is not admin`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT) }
            assertFalse(adminRepository.isAdmin(newUser))
        }
    }
}