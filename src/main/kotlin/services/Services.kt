package isel.leic.group25.services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.services.email.SmtpEmailService
import isel.leic.group25.services.email.model.EmailConfig

class Services(repositories: Repositories, jwtConfig: JwtConfig) {
    val emailConfig = EmailConfig(
        System.getenv("EMAIL_HOST"),
        port = System.getenv("EMAIL_PORT").toInt(),
        username = System.getenv("EMAIL_USERNAME"),
        password = System.getenv("EMAIL_PASSWORD"),
        from = System.getenv("EMAIL_FROM"),
        useSsl = System.getenv("EMAIL_USE_SSL").toBoolean(),
        baseUrl = System.getenv("FRONTEND_URL")
    )
    val frontendUrl = System.getenv("FRONTEND_URL") ?: throw NullPointerException()
    val classService = ClassService(repositories, repositories.ktormCommand)
    val userService = UserService(repositories, repositories.ktormCommand)
    val teacherRoomService = TeacherRoomService(repositories, repositories.ktormCommand)
    val emailService = SmtpEmailService(emailConfig)
    val authService = AuthService(repositories,
        repositories.ktormCommand, jwtConfig, emailService, frontendUrl)
    val subjectService = SubjectService(repositories, repositories.ktormCommand)
    val userClassService = UserClassService(repositories, repositories.ktormCommand)
    val roomService = RoomService(repositories, repositories.ktormCommand)
    val lectureService = LectureService(repositories, repositories.ktormCommand, emailService)
    val issueReportService = IssuesReportService(repositories, repositories.ktormCommand)

    fun <S, T> from(serviceSelector: Services.() -> S, block: S.() -> T): T{
        val service = this.serviceSelector()
        return service.block()
    }
}