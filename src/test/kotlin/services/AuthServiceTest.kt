package services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.users.AdminRepository
import isel.leic.group25.db.repositories.users.RoleApprovalRepository
import isel.leic.group25.db.repositories.users.UserRepository
import isel.leic.group25.services.AuthService
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import repositories.DatabaseTestSetup
import services.mocks.MockEmailService
import kotlin.test.*

class AuthServiceTest {
    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val adminRepository = AdminRepository(DatabaseTestSetup.database)
    private val roleApprovalRepository = RoleApprovalRepository(DatabaseTestSetup.database)
    private val transactionInterface = KTransaction(DatabaseTestSetup.database)

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
            role = "student"
        )

        assertTrue(result is Success, "Registration should succeed")
        val check = result.value
        assertTrue(check, "Registration should return true if student is created and email is sent")
    }

    @Test
    fun `register should fail with invalid role`() {
        val result = authService.register(
            email = "student@test.com",
            username = "student1",
            password = "SecurePass123!",
            role = "MARIO"
        )
        assertTrue(result is Failure, "Registration should fail with invalid role")
        assertEquals(
            result.value,
            AuthError.InvalidRole,
            "Error should be InvalidRole"
        )
    }

    @Test
    fun `register should fail with duplicated email`() {
        authService.register(
            email = "student@test.com",
            username = "student1",
            password = "SecurePass123!",
            role = "student"
        )
        val result = authService.register(
            email = "student@test.com",
            username = "student2",
            password = "SecurePass123!",
            role = "student"
        )
        assertTrue(result is Failure, "Registration should fail with duplicated email")
        assertEquals(
            result.value,
            AuthError.UserAlreadyExists,
            "Error should be UserAlreadyExists"
        )
    }

    @Test
    fun `register should fail with missing credentials`() {
        val emptyEmailResult = authService.register("", "student1", "pass", "student")
        val emptyUsernameResult = authService.register("student@test.com", "", "pass", "student")
        val emptyPasswordResult = authService.register("student@test.com", "student1", "", "student")

        assertTrue(emptyEmailResult is Failure, "Registration should fail with empty email")
        assertTrue(emptyUsernameResult is Failure, "Registration should fail with empty username")
        assertTrue(emptyPasswordResult is Failure, "Registration should fail with empty password")

        assertEquals(
            emptyEmailResult.value,
            AuthError.MissingCredentials,
            "Error should be MissingCredentials"
        )
        assertEquals(
            emptyUsernameResult.value,
            AuthError.MissingCredentials,
            "Error should be MissingCredentials"
        )
        assertEquals(
            emptyPasswordResult.value,
            AuthError.MissingCredentials,
            "Error should be MissingCredentials"
        )
    }

    @Test
    fun `register should fail with insecure password`() {
        val result = authService.register(
            email = "student@test.com",
            username = "student2",
            password = "NotSecureEnough",
            role = "student"
        )

        assertTrue(result is Failure, "Registration should fail with insecure password")
        assertEquals(
            result.value,
            AuthError.InsecurePassword,
            "Error should be InsecurePassword"
        )
    }

    @Test
    fun `login should return token with valid credentials`() {
        authService.register(
            email = "student@test.com",
            username = "student1",
            password = "SecurePass123!",
            role = "student"
        )
        val pendingApprovals = authService.getAllPendingApprovals(null, null)
        assertTrue(pendingApprovals is Success)
        assertNotNull(pendingApprovals.value.first())
        authService.verifyStudentAccount(pendingApprovals.value.first().verificationToken)
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
            role = "student"
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
            role = "student"
        )
        val result = authService.login("bowser@test.com", "SecurePass123!")
        assertTrue(result is Failure, "Login should fail for non-existent user")
        assertEquals(
            result.value,
            AuthError.UserNotFound,
            "Error should be UserNotFound"
        )
    }

    @Test
    fun `login should fail with empty credentials`() {
        val emptyEmailResult = authService.login("", "password")
        val emptyPassResult = authService.login("email@test.com", "")

        assertTrue(emptyEmailResult is Failure, "Login should fail with empty email")
        assertTrue(emptyPassResult is Failure, "Login should fail with empty password")

        assertEquals(
            emptyEmailResult.value,
            AuthError.MissingCredentials,
            "Error should be MissingCredentials"
        )
        assertEquals(
            emptyPassResult.value,
            AuthError.MissingCredentials,
            "Error should be MissingCredentials"
        )
    }
}