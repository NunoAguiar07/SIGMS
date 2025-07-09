package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.users.UserRepository
import repositories.DatabaseTestSetup
import kotlin.test.*

class UserRepositoryTest {

    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)
    private val kTormCommand = KtormCommand(DatabaseTestSetup.database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    private fun createUniversity() = universityRepository.createUniversity("TestUniversity")

    @Test
    fun `Should find user by id`() = kTormCommand.useTransaction {
        val uni = createUniversity()
        val newUser = userRepository.createWithRole("email@test.com", "tester", "pass", Role.STUDENT, uni, "local")
        val found = userRepository.findById(newUser.id)
        assertNotNull(found)
        assertEquals(newUser.id, found.id)
    }

    @Test
    fun `Should find user by email`() = kTormCommand.useTransaction {
        val uni = createUniversity()
        val newUser = userRepository.createWithRole("find@test.com", "tester", "pass", Role.STUDENT, uni, "local")
        val found = userRepository.findByEmail("find@test.com")
        assertNotNull(found)
        assertEquals(newUser.id, found.id)
    }

    @Test
    fun `Should update the user username`() = kTormCommand.useTransaction {
        val uni = createUniversity()
        val newUser = userRepository.createWithRole("update@test.com", "tester", "pass", Role.STUDENT, uni, "local")
        newUser.username = "updatedTester"
        userRepository.update(newUser)
        val found = userRepository.findById(newUser.id)
        assertEquals("updatedTester", found?.username)
    }

    @Test
    fun `Should create user without role`() = kTormCommand.useTransaction {
        val uni = createUniversity()
        val newUser = userRepository.createWithoutRole("nobody@test.com", "nobody", "pass", uni, "local")
        assertNotNull(newUser)
        assertEquals("nobody@test.com", newUser.email)
        assertNull(userRepository.getRoleById(newUser.id))
    }

    @Test
    fun `Should associate existing user with role`() = kTormCommand.useTransaction {
        val uni = createUniversity()
        val user = userRepository.createWithoutRole("late@test.com", "lateUser", "pass", uni, "local")
        userRepository.associateWithRole(user, Role.TEACHER)
        assertEquals(Role.TEACHER, userRepository.getRoleById(user.id))
    }

    @Test
    fun `Should get correct role by user id`() = kTormCommand.useTransaction {
        val uni = createUniversity()
        val user = userRepository.createWithRole("role@test.com", "roleUser", "pass", Role.TECHNICAL_SERVICE, uni, "local")
        val role = userRepository.getRoleById(user.id)
        assertEquals(Role.TECHNICAL_SERVICE, role)
    }

    @Test
    fun `Should delete user and its role`() = kTormCommand.useTransaction {
        val uni = createUniversity()
        val user = userRepository.createWithRole("delete@test.com", "deleteMe", "pass", Role.ADMIN, uni, "local")
        val result = userRepository.delete(user.id)
        assertTrue(result)
        assertNull(userRepository.findById(user.id))
        assertNull(userRepository.getRoleById(user.id))
    }

    @Test
    fun `Should return false if user does not exist when deleting`() = kTormCommand.useTransaction {
        val result = userRepository.delete(-999)
        assertFalse(result)
    }
}
