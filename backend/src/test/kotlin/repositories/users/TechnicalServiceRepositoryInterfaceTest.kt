package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.users.TechnicalServiceRepository
import isel.leic.group25.db.repositories.users.UserRepository
import repositories.DatabaseTestSetup
import kotlin.test.*

class TechnicalServiceRepositoryInterfaceTest {

    private val technicalServiceRepository = TechnicalServiceRepository(DatabaseTestSetup.database)
    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)
    private val kTormCommand = KtormCommand(DatabaseTestSetup.database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    private fun createTechnicalService(email: String = "tech@test.com"): User = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("TestUni")
        return@useTransaction userRepository.createWithRole(
            email = email,
            username = "techUser",
            password = User.hashPassword("pass"),
            role = Role.TECHNICAL_SERVICE,
            university = university,
            authProvider = "local"
        )
    }

    @Test
    fun `Should create a new technical service and find it by id`() = kTormCommand.useTransaction {
        val user = createTechnicalService()
        val tech = technicalServiceRepository.findTechnicalServiceById(user.id)
        assertNotNull(tech)
        assertEquals(user.id, tech.user.id)
    }

    @Test
    fun `Should return null for invalid technical service id`() = kTormCommand.useTransaction {
        val tech = technicalServiceRepository.findTechnicalServiceById(-1)
        assertNull(tech)
    }

    @Test
    fun `Should create a new technical service and find it by email`() = kTormCommand.useTransaction {
        val user = createTechnicalService("findme@test.com")
        val tech = technicalServiceRepository.findTechnicalServiceByEmail("findme@test.com")
        assertNotNull(tech)
        assertEquals(user.id, tech.user.id)
    }

    @Test
    fun `Should return null for invalid technical service email`() = kTormCommand.useTransaction {
        val tech = technicalServiceRepository.findTechnicalServiceByEmail("doesnotexist@test.com")
        assertNull(tech)
    }

    @Test
    fun `Should verify user is technical service`() = kTormCommand.useTransaction {
        val user = createTechnicalService()
        assertTrue(technicalServiceRepository.isTechnicalService(user))
    }

    @Test
    fun `Should verify user is not technical service`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("TestUni")
        val user = userRepository.createWithRole(
            email = "admin@test.com",
            username = "adminUser",
            password = User.hashPassword("pass"),
            role = Role.ADMIN,
            university = university,
            authProvider = "local"
        )
        assertFalse(technicalServiceRepository.isTechnicalService(user))
    }

    @Test
    fun `Should return list of technical services for a university`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("TechUni")
        val user1 = userRepository.createWithRole("tech1@test.com", "t1", User.hashPassword("pass"), Role.TECHNICAL_SERVICE, university, "local")
        val user2 = userRepository.createWithRole("tech2@test.com", "t2", User.hashPassword("pass"), Role.TECHNICAL_SERVICE, university, "local")

        val services = technicalServiceRepository.universityTechnicalServices(university.id)
        assertEquals(2, services.size)
        assertTrue(services.any { it.user.id == user1.id })
        assertTrue(services.any { it.user.id == user2.id })
    }

    @Test
    fun `Should return empty list when no technical services exist for university`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("EmptyUni")
        val services = technicalServiceRepository.universityTechnicalServices(university.id)
        assertTrue(services.isEmpty())
    }
}
