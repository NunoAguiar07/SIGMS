package services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.services.AuthService
import mocks.repositories.timetables.MockUniversityRepository
import mocks.repositories.users.MockAdminRepository
import mocks.repositories.users.MockRoleApprovalRepository
import mocks.repositories.users.MockUserRepository
import mocks.repositories.utils.MockTransaction
import mocks.services.MockEmailService
import repositories.DatabaseTestSetup
import kotlin.test.AfterTest

class AuthServiceTest {
    private val userRepository = MockUserRepository()
    private val adminRepository = MockAdminRepository(userRepository)
    private val universityRepository = MockUniversityRepository()
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
        universityRepository = universityRepository,
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

}