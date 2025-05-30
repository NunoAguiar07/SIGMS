package services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.services.AuthService
import mocks.repositories.MockRepositories
import mocks.repositories.utils.MockTransaction
import mocks.services.MockEmailService
import org.ktorm.database.Database
import repositories.DatabaseTestSetup
import kotlin.test.AfterTest

class AuthServiceTest {
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

    val mockDB = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        password = ""
    )
    private val mockRepositories = MockRepositories(mockDB)

    private val mockEmailService = MockEmailService()


    private val authService = AuthService(
        repositories = mockRepositories ,
        transactionable = transactionInterface,
        jwtConfig = jwtConfig,
        emailService = mockEmailService,
        frontendUrl = frontendUrl
    )

}