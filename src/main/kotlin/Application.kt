package isel.leic.group25

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.jetty.jakarta.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import isel.leic.group25.api.http.configureRouting
import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.repositories.issues.IssueReportRepository
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.timetables.LectureRepository
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.users.*
import isel.leic.group25.services.*
import isel.leic.group25.services.email.SmtpEmailService
import isel.leic.group25.services.email.model.EmailConfig
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect
import kotlin.time.Duration.Companion.seconds

val applicationHttpClient = HttpClient(OkHttp)

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(CORS) {
        //allowSameOrigin
        allowHost("localhost:8081", schemes = listOf("http")) // Expo front end
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)

        // Allow headers
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        allowCredentials = true

        // Max age for preflight requests
        maxAgeInSeconds = 24 * 60 * 60
    }
    install(ContentNegotiation) {
        json()
    }

    val db = Database.connect(
        url = System.getenv("DB_URL"),
        user = System.getenv("DB_USER"),
        password = System.getenv("DB_PASSWORD"),
        driver = "org.postgresql.Driver",
        dialect = PostgreSqlDialect()
    )

    val emailConfig = EmailConfig(
        System.getenv("EMAIL_HOST"),
        port = System.getenv("EMAIL_PORT").toInt(),
        username = System.getenv("EMAIL_USERNAME"),
        password = System.getenv("EMAIL_PASSWORD"),
        from = System.getenv("EMAIL_FROM"),
        useSsl = System.getenv("EMAIL_USE_SSL").toBoolean(),
        baseUrl = System.getenv("FRONTEND_URL")
    )

    val secret = System.getenv("JWT_SECRET")
    val issuer = System.getenv("JWT_ISSUER")
    val audience = System.getenv("JWT_AUDIENCE")
    val myRealm = System.getenv("JWT_REALM")

    val frontendUrl = System.getenv("FRONTEND_URL")

    val jwtConfig = JwtConfig(secret, issuer, audience, myRealm)
    val kTransaction = KTransaction(db)
    val universityRepository = UniversityRepository(db)
    val userRepository = UserRepository(db)
    val studentRepository = StudentRepository(db)
    val teacherRepository = TeacherRepository(db)
    val adminRepository = AdminRepository(db)
    val technicalServiceRepository = TechnicalServiceRepository(db)
    val roleApprovalRepository = RoleApprovalRepository(db)
    val classRepository = ClassRepository(db)
    val subjectRepository = SubjectRepository(db)
    val roomRepository = RoomRepository(db)
    val lectureRepository = LectureRepository(db)
    val issueReportRepository = IssueReportRepository(db)

    val classService = ClassService(classRepository, subjectRepository, kTransaction)
    val userService = UserService(userRepository, universityRepository, kTransaction)
    val teacherRoomService = TeacherRoomService(teacherRepository, roomRepository, kTransaction)
    val emailService = SmtpEmailService(emailConfig)
    val authService = AuthService(
        userRepository,
        adminRepository,
        universityRepository,
        roleApprovalRepository,
        kTransaction,
        jwtConfig,
        emailService,
        frontendUrl
    )
    val subjectService = SubjectService(subjectRepository, universityRepository, kTransaction)
    val userClassService = UserClassService(
        userRepository,
        studentRepository,
        teacherRepository,
        classRepository,
        lectureRepository,
        kTransaction
    )
    val roomService = RoomService(roomRepository, universityRepository, kTransaction)
    val lectureService = LectureService(lectureRepository, kTransaction, classRepository, roomRepository, emailService)
    val issueReportService = IssuesReportService(
        issueReportRepository,
        userRepository,
        technicalServiceRepository,
        kTransaction,
        roomRepository
    )

    install(Authentication) {
        oauth("auth-microsoft") {
            client = applicationHttpClient
            urlProvider = { "${System.getenv("FRONTEND_URL")}/auth/microsoft/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "microsoft",
                    authorizeUrl = "https://login.microsoftonline.com/${System.getenv("MICROSOFT_TENANT_ID")}/oauth2/v2.0/authorize",
                    accessTokenUrl = "https://login.microsoftonline.com/${System.getenv("MICROSOFT_TENANT_ID")}/oauth2/v2.0/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("MICROSOFT_CLIENT_ID"),
                    clientSecret = System.getenv("MICROSOFT_CLIENT_SECRET"),
                    defaultScopes = listOf("openid", "profile", "email"),
                    extraAuthParameters = listOf(
                        "response_type" to "code",
                        "prompt" to "select_account"
                    )
                )
            }
        }
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                jwtConfig.buildVerifier()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                val role = credential.payload.getClaim("role").asString()
                val universityId = credential.payload.getClaim("universityId").asInt()
                if (userId != null && role != null && universityId != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token expired or invalid")
            }
        }
    }
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 30.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    configureRouting(
        userService,
        teacherRoomService,
        authService,
        classService,
        userClassService,
        subjectService,
        roomService,
        lectureService,
        issueReportService,
        applicationHttpClient
    )
}
