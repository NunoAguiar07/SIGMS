package services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.AuthService
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.MockRepositories
import mocks.repositories.utils.MockTransaction
import mocks.services.MockEmailService
import org.ktorm.database.Database
import kotlin.test.*

class AuthServiceTest {
    private val mockDB = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        password = ""
    )
    private val mockRepositories = MockRepositories(mockDB)
    private val transaction = MockTransaction()
    private val jwtConfig = JwtConfig(
        secret = "test-secret-1234567890-1234567890",
        issuer = "test-issuer",
        audience = "test-audience",
        realm = "test-realm",
        expirationTime = 3600L * 1000L
    )
    private val emailService = MockEmailService()
    private val frontendUrl = "http://localhost:8080"

    private val authService = AuthService(
        repositories = mockRepositories,
        transactionable = transaction,
        jwtConfig = jwtConfig,
        emailService = emailService,
        frontendUrl = frontendUrl
    )

    @Test
    fun `register succeeds with valid student data`() {
        val university = mockRepositories.ktormCommand.useTransaction {
            mockRepositories.from({ universityRepository }) {
                createUniversity("ISEL")
            }
        }

        val result = authService.register(
            email = "student@isel.pt",
            username = "student",
            password = "password123!",
            role = Role.STUDENT,
            universityId = university.id
        )

        assertTrue(result is Success, "Expected Success")
        assertTrue(result.value, "Expected value to be true")
    }

    @Test
    fun `register fails when email already exists`() {
        val university = mockRepositories.ktormCommand.useTransaction {
            mockRepositories.from({ universityRepository }) {
                createUniversity("ISEL")
            }
        }

        // Register first time
        authService.register(
            email = "student@isel.pt",
            username = "student",
            password = "password123!",
            role = Role.STUDENT,
            universityId = university.id
        )

        // Attempt second registration
        val result = authService.register(
            email = "student@isel.pt",
            username = "student2",
            password = "otherpass123!",
            role = Role.STUDENT,
            universityId = university.id
        )

        assertTrue(result is Failure, "Expected Failure")
        assertEquals(AuthError.UserAlreadyExists, result.value)
    }

    @Test
    fun `login succeeds with correct credentials`() {
        val university = mockRepositories.ktormCommand.useTransaction {
            mockRepositories.from({ universityRepository }) {
                createUniversity("ISEL")
            }
        }

        authService.register(
            email = "login@isel.pt",
            username = "loginuser",
            password = "password123!",
            role = Role.STUDENT,
            universityId = university.id
        )
        val user = mockRepositories.ktormCommand.useTransaction {
            mockRepositories.from({userRepository}) { findByEmail("login@isel.pt") }!!
        }
        mockRepositories.from({userRepository}) {
            associateWithRole(user, Role.STUDENT)
        }

        val result = authService.login("login@isel.pt", "password123!")
        assertTrue(result is Success, "Expected Success")
        assertTrue(result.value.isNotBlank(), "Token should not be blank")
    }

    @Test
    fun `login fails with incorrect password`() {
        val university = mockRepositories.ktormCommand.useTransaction {
            mockRepositories.from({ universityRepository }) {
                createUniversity("ISEL")
            }
        }

        authService.register(
            email = "wrongpass@isel.pt",
            username = "wronguser",
            password = "password123!",
            role = Role.STUDENT,
            universityId = university.id
        )

        val result = authService.login("wrongpass@isel.pt", "wrongPassword!")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(AuthError.InvalidCredentials, result.value)
    }

    @Test
    fun `login fails with non-existing user`() {
        val result = authService.login("nonexistent@isel.pt", "anyPassword")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(AuthError.UserNotFound, result.value)
    }
}
