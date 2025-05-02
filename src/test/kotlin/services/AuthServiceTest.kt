package services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.AuthService
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.users.MockUserRepository
import mocks.repositories.users.MockAdminRepository
import mocks.repositories.users.MockRoleApprovalRepository
import mocks.repositories.utils.MockTransaction
import mocks.services.MockEmailService
import repositories.DatabaseTestSetup
import kotlin.test.*

class AuthServiceTest {
    private val userRepository = MockUserRepository()
    private val adminRepository = MockAdminRepository(userRepository)
    private val roleApprovalRepository = MockRoleApprovalRepository()
    private val transactionInterface = MockTransaction()

    // Test JWT config
    private val jwtConfig = JwtConfig(
        secret = "test-secret-1234567890-1234567890",
        issuer = "test-issuer",
        audience = "test-audience",
        realm = "test-realm",
        expirationTime = 3600L * 1000L // 1 hour
    )

    // Test frontend URL
    private val frontendUrl = "http://localhost:8080"

    // Test Email Service
    private val mockEmailService = MockEmailService()

    private val authService = AuthService(
        userRepository = userRepository,
        adminRepository = adminRepository,
        roleApprovalRepository = roleApprovalRepository,
        transactionInterface = transactionInterface,
        jwtConfig = jwtConfig,
        emailService = mockEmailService,
        frontendUrl = frontendUrl
    )

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }


    // Test cases for AuthService
    @Test
    fun `register should return token for valid student registration`() {
        val result = authService.register(
            email = "student@test.com",
            username = "student1",
            password = "SecurePass123!",
            role = Role.STUDENT
        )

        assertTrue(result is Success, "Registration should succeed")
        val check = result.value
        assertTrue(check, "Registration should return true if student is created and email is sent")
    }

    @Test
    fun `register should fail with duplicated email`() {
        authService.register(
            email = "student@test.com",
            username = "student1",
            password = "SecurePass123!",
            role = Role.STUDENT
        )
        val result = authService.register(
            email = "student@test.com",
            username = "student2",
            password = "SecurePass123!",
            role = Role.STUDENT
        )
        assertTrue(result is Failure, "Registration should fail with duplicated email")
        assertEquals(
            result.value,
            AuthError.UserAlreadyExists,
            "Error should be UserAlreadyExists"
        )
    }

    @Test
    fun `login should return token with valid credentials`() {
        authService.register(
            email = "student@test.com",
            username = "student1",
            password = "SecurePass123!",
                role = Role.STUDENT
        )
        val pendingApprovals = authService.getAllPendingApprovals(5, 0)
        assertTrue(pendingApprovals is Success)
        assertNotNull(pendingApprovals.value.first())
        val tempToken = authService.verifyStudentAccount(pendingApprovals.value.first().verificationToken)
        assertTrue(tempToken is Success, "Account approved")
        val result = authService.login("student@test.com", "SecurePass123!")
        assertTrue(result is Success, "Login should succeed")

        val token = result.value
        // Verify the token
        val decodedToken = jwtConfig.buildVerifier()
            .verify(token)
        assertNotNull(decodedToken, "Decoded token should not be null")
        assertTrue(decodedToken.audience.contains("test-audience"), "Token audience should match")
        assertTrue(decodedToken.issuer == "test-issuer", "Token issuer should match")
        assertTrue(decodedToken.getClaim("role").asString() == "STUDENT", "Token role should match")
    }

    @Test
    fun `login should fail with wrong password`() {
        authService.register(
            email = "student@test.com",
            username = "student1",
            password = "SecurePass123!",
            role = Role.STUDENT
        )
        val result = authService.login("student@test.com", "Luigi")
        assertTrue(result is Failure, "Login should fail with wrong password")
        assertEquals(
            result.value,
            AuthError.InvalidCredentials,
            "Error should be InvalidCredentials"
        )
    }

    @Test
    fun `login should fail for non-existent user`() {
        authService.register(
            email = "student@test.com",
            username = "student1",
            password = "SecurePass123!",
            role = Role.STUDENT
        )
        val result = authService.login("bowser@test.com", "SecurePass123!")
        assertTrue(result is Failure, "Login should fail for non-existent user")
        assertEquals(
            result.value,
            AuthError.UserNotFound,
            "Error should be UserNotFound"
        )
    }
}