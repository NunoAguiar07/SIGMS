package isel.leic.group25

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.jetty.jakarta.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
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
import isel.leic.group25.db.repositories.users.AdminRepository
import isel.leic.group25.db.repositories.users.RoleApprovalRepository
import isel.leic.group25.db.repositories.users.UserRepository
import isel.leic.group25.services.*
import isel.leic.group25.services.email.SmtpEmailService
import isel.leic.group25.services.email.model.EmailConfig
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect
import kotlin.time.Duration.Companion.seconds

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    val db = Database.connect(url = "jdbc:postgresql://localhost:5432/sigms", user = "user", password = "password123", driver = "org.postgresql.Driver", dialect = PostgreSqlDialect())

    val emailConfig = EmailConfig(
        host = environment.config.property("email.host").getString(),
        port = environment.config.property("email.port").getString().toInt(),
        username = environment.config.property("email.username").getString(),
        password = environment.config.property("email.password").getString(),
        from = environment.config.property("email.from").getString(),
        useSsl = environment.config.property("email.useSsl").getString().toBoolean(),
        baseUrl = environment.config.property("frontend.url").getString()
    )

    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()

    val frontendUrl = environment.config.property("frontend.url").getString()

    val jwtConfig = JwtConfig(secret, issuer, audience, myRealm)
    val kTransaction = KTransaction(db)
    val userRepository = UserRepository(db)
    val adminRepository = AdminRepository(db)
    val roleApprovalRepository = RoleApprovalRepository(db)
    val classRepository = ClassRepository(db)
    val subjectRepository = SubjectRepository(db)
    val roomRepository = RoomRepository(db)
    val lectureRepository = LectureRepository(db)
    val issueReportRepository = IssueReportRepository(db)

    val classService = ClassService(classRepository, subjectRepository, kTransaction)
    val userService = UserService(userRepository, kTransaction)
    val emailService = SmtpEmailService(emailConfig)
    val authService = AuthService(userRepository, adminRepository, roleApprovalRepository, kTransaction, jwtConfig, emailService, frontendUrl)
    val subjectService = SubjectService(subjectRepository, kTransaction)
    val userClassService = UserClassService(userRepository, classRepository, lectureRepository, kTransaction)
    val roomService = RoomService(roomRepository, kTransaction)
    val lectureService = LectureService(lectureRepository, kTransaction, classRepository, roomRepository)
    val issueReportService = IssuesReportService(issueReportRepository, kTransaction, roomRepository)

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                jwtConfig.buildVerifier()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null) {
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
    configureRouting(userService, authService, classService, userClassService, subjectService, roomService, lectureService, issueReportService)
}
