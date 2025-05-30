package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.users.UserRepository
import repositories.DatabaseTestSetup
import kotlin.test.*

class UserRepositoryTest {

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)
    private val kTormCommand = KtormCommand(DatabaseTestSetup.database)
/*
    @Test
    fun `Should create a new user for every role`(){
        kTransaction.useTransaction {
            var userCount = 1
            for(role in Role.entries){
                val user = User {
                    email = "testemail${role.name}@test.com"
                    username = "tester${role.name}"
                    password = User.hashPassword("test")
                    profileImage = byteArrayOf()
                }.let { userRepository.create(it, role) }
                assertEquals(user.id, userCount)
                assertEquals(user.email, "testemail${role.name}@test.com")
                assertEquals(user.username, "tester${role.name}")
                assertTrue(User.verifyPassword(user.password, "test"))
                assertContentEquals(user.profileImage, byteArrayOf())
                assertDoesNotThrow {
                    when(role){
                        Role.STUDENT -> {
                            user.toStudent(database)
                        }
                        Role.TEACHER -> {
                            user.toTeacher(database)
                        }
                        Role.ADMIN -> {
                            user.toAdmin(database)
                        }
                        Role.TECHNICAL_SERVICE -> {
                            user.toTechnicalService(database)
                        }
                    }
                }
                userCount += 1
            }
        }
    }
*/
    @Test
    fun `Should find user my id`() {
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
            val user = userRepository.findById(newUser.id)
            assertNotNull(user)
            assertEquals(newUser.id, user.id)
            assertEquals(newUser.email, user.email)
            assertEquals(newUser.username, user.username)
            assertContentEquals(newUser.profileImage, user.profileImage)
        }
    }

    @Test
    fun `Should find user my email`(){
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
            val user = userRepository.findByEmail(newUser.email)
            assertNotNull(user)
            assertEquals(newUser.id, user.id)
            assertEquals(newUser.email, user.email)
            assertEquals(newUser.username, user.username)
            assertContentEquals(newUser.profileImage, user.profileImage)
        }
    }

    @Test
    fun `Should update the user username`(){
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
            val newUsername = "newTester"
            newUser.username = newUsername
            userRepository.update(newUser)
            val user = userRepository.findById(newUser.id)
            assertNotNull(user)
            assertEquals(newUsername, user.username)
        }
    }
}