package services

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.services.UserService
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.MockRepositories
import mocks.repositories.users.MockUserRepository
import org.ktorm.database.Database
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserServiceTest {
    private val mockDB = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        password = ""
    )
    private val mockRepositories = MockRepositories(mockDB)
    private val userService = UserService(mockRepositories, mockRepositories.ktormCommand)

    private fun createTestUser(role: Role = Role.STUDENT): User {
        return mockRepositories.ktormCommand.useTransaction {
            val newUniversity = mockRepositories.from({universityRepository}){
                createUniversity("Test University")
            }
            val user = User {
                email = "test@test.com"
                username = "testuser"
                password = User.hashPassword("test123!")
                profileImage = byteArrayOf(1, 2, 3)
                authProvider = "local"
                university = newUniversity
            }.let {
                mockRepositories.from({userRepository}){
                    createWithRole(it.email, it.username, it.password, role, it.university, it.authProvider)
                }
            }
            return@useTransaction user
        }
    }

    @Test
    fun `getUserById should return user when exists`() {
        val user = createTestUser()
        val result = userService.getUserById(user.id)
        assertTrue(result is Success, "Expected success result")
        val getUser = result.value
        assertEquals(user.id, getUser.id, "User ID should match")
        assertEquals(user.email, getUser.email, "User email should match")
    }

    @Test
    fun `getUserById should return UserNotFound when user doesn't exist`() {
        createTestUser()
        val result = userService.getUserById(33)
        assertTrue(result is Failure, "User with this id should not exist")
        assertEquals(AuthError.UserNotFound, result.value, "Expected UserNotFound error")
    }

    @Test
    fun `updateUser should update username when provided`() {
        val user = createTestUser()
        val newUsername = "Wario"
        val result = userService.updateUser(user.id, newUsername, null)
        assertTrue(result is Success, "Expected success result")
        val updatedUser = result.value
        assertEquals(newUsername, updatedUser.username, "Username should be updated")
    }

    @Test
    fun `updateUser should update image when provided`() {
        val user = createTestUser()
        val newImage = byteArrayOf(4, 5, 6)
        val result = userService.updateUser(user.id, null, newImage)
        assertTrue(result is Success, "Expected success result")
        val updatedUser = result.value
        assertTrue(updatedUser.profileImage.contentEquals(newImage), "Profile image should be updated")
    }

    @Test
    fun `updateUser should update both username and image when provided`() {
        val user = createTestUser()
        val newUsername = "Wario"
        val newImage = byteArrayOf(4, 5, 6)
        val result = userService.updateUser(user.id, newUsername, newImage)
        assertTrue(result is Success, "Expected success result")
        val updatedUser = result.value
        assertEquals(newUsername, updatedUser.username, "Username should be updated")
        assertTrue(updatedUser.profileImage.contentEquals(newImage), "Profile image should be updated")
    }

    @Test
    fun `updateUser should return UserNotFound when user doesn't exist`() {
        val newUsername = "Wario"
        val newImage = byteArrayOf(4, 5, 6)
        val result = userService.updateUser(33, newUsername, newImage)
        assertTrue(result is Failure, "User with this id should not exist")
        assertEquals(AuthError.UserNotFound, result.value, "Expected UserNotFound error")
    }

    @Test
    fun `changePassword should return InvalidCredentials when old password is wrong`() {
        val user = createTestUser()
        val newPassword = "NewPassword123!"
        val result = userService.changePassword(user.id, "wrongpassword", newPassword)
        assertTrue(result is Failure, "Expected InvalidCredentials error")
        assertEquals(AuthError.InvalidCredentials, result.value, "Expected InvalidCredentials error")
    }

    @Test
    fun `changePassword should return UserNotFound when user doesn't exist`() {
        val newPassword = "NewPassword123!"
        val result = userService.changePassword(33, "test123!", newPassword)
        assertTrue(result is Failure, "User with this id should not exist")
        assertEquals(AuthError.UserNotFound, result.value, "Expected UserNotFound error")
    }

    @Test
    fun `getRoleByUserId should return role when user exists`() {
        val user = createTestUser(Role.TEACHER)
        val result = userService.getRoleByUserId(user.id)
        assertTrue(result is Success, "Expected success result")
        val role = result.value
        assertEquals(Role.TEACHER, role, "Role should match")
    }

    @Test
    fun `getRoleByUserId should return UserNotFound when user doesn't exist`() {
        val result = userService.getRoleByUserId(33)
        assertTrue(result is Failure, "User with this id should not exist")
        assertEquals(AuthError.UserNotFound, result.value, "Expected UserNotFound error")
    }
    @Test
    fun `changePassword should succeed when old password is correct`() {
        val user = createTestUser().apply {
            password = User.hashPassword("test123!")
            mockRepositories.from({userRepository as MockUserRepository}) { update(this@apply) }
        }
        val newPassword = "newSecure123!"

        val result = userService.changePassword(user.id, "test123!", newPassword)
        assertTrue(result is Success, "Expected success result")
        val updatedUser = result.value

        assertTrue(User.verifyPassword(updatedUser.password, newPassword), "Password should match the new one")
    }

    @Test
    fun `deleteUser should succeed when user exists`() {
        val user = createTestUser()
        val result = userService.deleteUser(user.id)

        assertTrue(result is Success)
        assertTrue(result.value)

        val getResult = userService.getUserById(user.id)
        assertTrue(getResult is Failure)
        assertEquals(AuthError.UserNotFound, getResult.value)
    }

    @Test
    fun `deleteUser should return UserNotFound when user doesn't exist`() {
        val result = userService.deleteUser(9999)
        assertTrue(result is Failure)
        assertEquals(AuthError.UserNotFound, result.value)
    }
}