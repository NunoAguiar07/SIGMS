package isel.leic.group25.services

import UniversityRepositoryInterface
import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.Status
import isel.leic.group25.db.entities.users.RoleApproval
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.users.interfaces.AdminRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.RoleApprovalRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.UserRepositoryInterface
import isel.leic.group25.services.email.EmailService
import isel.leic.group25.services.email.model.UserDetails
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import org.apache.commons.mail.EmailException
import java.sql.SQLException
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

typealias RegisterResult = Either<AuthError, Boolean>

typealias LoginResult = Either<AuthError, String>

typealias PendingRolesResult = Either<AuthError, List<RoleApproval>>

typealias PendingRoleResult = Either<AuthError, RoleApproval>

typealias AssessResult = Either<AuthError, Boolean>

typealias VerificationResult = Either<AuthError, String>

class AuthService(
    private val userRepository: UserRepositoryInterface,
    private val adminRepository: AdminRepositoryInterface,
    private val universityRepository: UniversityRepositoryInterface,
    private val roleApprovalRepository: RoleApprovalRepositoryInterface,
    private val transactionInterface: TransactionInterface,
    private val jwtConfig: JwtConfig,
    private val emailService: EmailService,
    private val frontendUrl: String
) {

    private inline fun <T> runCatching(block: () -> Either<AuthError, T>): Either<AuthError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(AuthError.ConnectionDbError(e.message))
        } catch (e: EmailException) {
            failure(AuthError.EmailError(e.message))
        }
    }

    fun authenticateWithMicrosoft(username: String, email:String, universityName: String): LoginResult {
        return runCatching {
            transactionInterface.useTransaction {
                val user = userRepository.findByEmail(email)
                return@useTransaction if (user != null) {
                    if (user.authProvider == "local") {
                        return@useTransaction failure(AuthError.MicrosoftAccountRequired)
                    }
                    val token = jwtConfig.generateToken(user.id, Role.STUDENT.name)
                    success(token)
                } else {
                    val university = universityRepository.getUniversityByName(universityName)
                        ?: return@useTransaction failure(AuthError.UniversityNotFound)
                    val newUser = userRepository.createWithoutRole(email, username, "", university, "microsoft")
                    userRepository.associateWithRole(newUser, Role.STUDENT)
                    val token = jwtConfig.generateToken(newUser.id, Role.STUDENT.name)
                    success(token)
                }
            }
        }
    }

    fun register(email: String, username: String, password: String, role:Role, universityId: Int): RegisterResult {
        val verificationToken = generateVerificationToken()
        return runCatching {
            transactionInterface.useTransaction {
                if(userRepository.findByEmail(email) != null) {
                    return@useTransaction failure(AuthError.UserAlreadyExists)
                }
                val university = universityRepository.getUniversityById(universityId)
                    ?: return@useTransaction failure(AuthError.UniversityNotFound)
                val user = userRepository.createWithoutRole(email, username, password, university, "local")
                if(!roleApprovalRepository.addPendingApproval(user, role, verificationToken, null)) {
                    return@useTransaction failure(AuthError.RoleApprovedFailed)
                }
                return@useTransaction when(role) {
                    Role.STUDENT -> {
                        val verificationLink = "${frontendUrl}verify-account?token=$verificationToken"
                        emailService.sendStudentVerificationEmail(
                            userEmail = user.email,
                            username = user.username,
                            verificationLink = verificationLink
                        )
                         success(true)
                    }
                    Role.TEACHER, Role.TECHNICAL_SERVICE -> {
                        val adminEmails = adminRepository.getAllAdminEmails()
                        val approvalLink = "${frontendUrl}approve-request?id=$verificationToken"
                        val userDetails = UserDetails(
                            username = user.username,
                            email = user.email,
                            requestedRole = role.name,
                        )
                        if(adminEmails.isNotEmpty()){
                            emailService.sendAdminApprovalRequest(adminEmails, approvalLink, userDetails)
                        }
                        success(false)
                    }
                    else -> failure(AuthError.UnauthorizedRole)
                }
            }
        }
    }

    fun getAllPendingApprovals(limit:Int, offset:Int): PendingRolesResult {
        return runCatching {
            transactionInterface.useTransaction {
                val approvals = roleApprovalRepository.getApprovals(limit, offset)
                return@useTransaction success(approvals)
            }
        }
    }

    fun getApprovalByToken(token: String): PendingRoleResult {
        return runCatching {
            transactionInterface.useTransaction {
                val roleApproval = roleApprovalRepository.getApprovalByToken(token)
                    ?: return@useTransaction failure(AuthError.TokenValidationFailed)
                return@useTransaction success(roleApproval)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun assessRoleRequest(token: String, adminUserId: Int, status: Status): AssessResult {
        return runCatching {
            transactionInterface.useTransaction {
                val roleApproval = roleApprovalRepository.getApprovalByToken(token)
                    ?: return@useTransaction failure(AuthError.TokenValidationFailed)
                if(roleApproval.status != Status.PENDING) {
                    return@useTransaction failure(AuthError.AlreadyProcessed)
                }
                if(roleApproval.expiresAt.minus(Clock.System.now()).isNegative()) {
                    return@useTransaction failure(AuthError.TokenValidationFailed)
                }
                val admin = adminRepository.findAdminById(adminUserId)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                if(!adminRepository.isAdmin(admin.user)) {
                    return@useTransaction failure(AuthError.UserNotFound)
                }
                roleApproval.verifiedBy = admin
                roleApproval.status = status
                if(!roleApprovalRepository.updateApproval(roleApproval)) {
                    return@useTransaction failure(AuthError.RoleApprovedFailed)
                }
                if(status == Status.REJECTED) {
                    emailService.sendUserApprovalNotification(roleApproval.user.email, false)
                    return@useTransaction success(true)
                }
                val user = userRepository.findById(roleApproval.user.id)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                val newRole = roleApproval.requestedRole
                when(newRole) {
                    Role.TEACHER -> {
                        userRepository.associateWithRole(user, Role.TEACHER)
                        emailService.sendUserApprovalNotification(user.email, true)
                    }
                    Role.TECHNICAL_SERVICE -> {
                        userRepository.associateWithRole(user, Role.TECHNICAL_SERVICE)
                        emailService.sendUserApprovalNotification(user.email, true)
                    }
                    else -> {
                        return@useTransaction failure(AuthError.UnauthorizedRole)
                    }
                }
                return@useTransaction success(true)
            }
        }
    }

    fun login(email: String, password: String): LoginResult {
        return runCatching {
            transactionInterface.useTransaction {
                val user = userRepository.findByEmail(email)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                if (user.authProvider == "microsoft") {
                    return@useTransaction failure(AuthError.MicrosoftLoginRequired)
                }
                if(!User.verifyPassword(user.password, password)) {
                    return@useTransaction failure(AuthError.InvalidCredentials)
                }
                val role = userRepository.getRoleById(user.id)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                val token = jwtConfig.generateToken(user.id, role.name)
                return@useTransaction success(token)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun verifyStudentAccount(token: String): VerificationResult {
        return runCatching {
            transactionInterface.useTransaction {
                val roleApproval = roleApprovalRepository.getApprovalByToken(token)
                    ?: return@useTransaction failure(AuthError.TokenValidationFailed)
                if (roleApproval.requestedRole != Role.STUDENT) {
                    return@useTransaction failure(AuthError.UnauthorizedRole)
                }
                if (roleApproval.status != Status.PENDING) {
                    return@useTransaction failure(AuthError.AlreadyProcessed)
                }
                if (roleApproval.expiresAt.minus(Clock.System.now()).isNegative()) {
                    return@useTransaction failure(AuthError.TokenValidationFailed)
                }
                val user = userRepository.findById(roleApproval.user.id)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                roleApproval.status = Status.APPROVED
                if (!roleApprovalRepository.updateApproval(roleApproval)) {
                    return@useTransaction failure(AuthError.RoleApprovedFailed)
                }
                userRepository.associateWithRole(user, Role.STUDENT)
                val jwtToken = jwtConfig.generateToken(user.id, Role.STUDENT.name)
                return@useTransaction success(jwtToken)
            }
        }
    }

    private fun generateVerificationToken(): String {
        return UUID.randomUUID().toString()
    }
}