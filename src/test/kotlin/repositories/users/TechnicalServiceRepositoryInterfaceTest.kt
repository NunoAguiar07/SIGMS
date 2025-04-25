package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.users.TechnicalServiceRepository
import isel.leic.group25.db.repositories.users.UserRepository
import repositories.DatabaseTestSetup
import kotlin.test.*

class TechnicalServiceRepositoryInterfaceTest {

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    private val technicalServiceRepository = TechnicalServiceRepository(DatabaseTestSetup.database)
    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val kTransaction = KTransaction(DatabaseTestSetup.database)

    @Test
    fun `Should create a new technical service and find it by id`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.TECHNICAL_SERVICE) }
            val technicalService = technicalServiceRepository.findTechnicalServiceById(newUser.id)
            assertNotNull(technicalService)
            assertEquals(newUser.id, technicalService.user.id)
        }
    }

    @Test
    fun `Should create a new technical service and find it by email`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.TECHNICAL_SERVICE) }
            val technicalService = technicalServiceRepository.findTechnicalServiceByEmail(newUser.email)
            assertNotNull(technicalService)
            assertEquals(newUser.id, technicalService.user.id)
        }
    }

    @Test
    fun `Should verify user is technical service`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.TECHNICAL_SERVICE) }
            assertTrue(technicalServiceRepository.isTechnicalService(newUser))
        }
    }

    @Test
    fun `Should verify user is not technical service`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.ADMIN) }
            assertFalse(technicalServiceRepository.isTechnicalService(newUser))
        }
    }
}