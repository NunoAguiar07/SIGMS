package isel.leic.group25.services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.Status
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.entities.users.RoleApproval
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
import java.util.*
import kotlin.math.absoluteValue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

typealias RegisterResult = Either<AuthError, Boolean>

typealias LoginResult = Either<AuthError, String>

typealias PendingRolesResult = Either<AuthError, List<RoleApproval>>

typealias PendingRoleResult = Either<AuthError, RoleApproval>

typealias AssesResult = Either<AuthError, Boolean>

typealias VerificationResult = Either<AuthError, String>

class AuthService(
    private val userRepository: UserRepositoryInterface,
    private val adminRepository: AdminRepositoryInterface,
    private val roleApprovalRepository: RoleApprovalRepositoryInterface,
    private val transactionInterface: TransactionInterface,
    private val jwtConfig: JwtConfig,
    private val emailService: EmailService,
    private val frontendUrl: String
) {
    fun register(email: String, username: String, password: String, role:String): RegisterResult {
        if(email.isBlank() || username.isBlank() || password.isBlank()) {
            return failure(AuthError.MissingCredentials)
        }
        if(User.isNotSecurePassword(password)) {
            return failure(AuthError.InsecurePassword)
        }
        val actualRole = role.uppercase(Locale.getDefault())
        if(actualRole !in Role.entries.toString()) return failure(AuthError.InvalidRole)
        return transactionInterface.useTransaction {
            if(userRepository.findByEmail(email) != null) {
                return@useTransaction failure(AuthError.UserAlreadyExists)
            }
            val user = userRepository.createWithoutRole(email, username, password)
            val verificationToken = generateVerificationToken()
            val requestedRole = Role.valueOf(actualRole)
            if(!roleApprovalRepository.addPendingApproval(user, requestedRole, verificationToken, null)) {
                return@useTransaction failure(AuthError.RoleApprovedFailed)
            }
            when(role) {
                "student" -> {
                    val verificationLink = "${frontendUrl}verify-account?token=$verificationToken"
                    emailService.sendStudentVerificationEmail(
                        userEmail = user.email,
                        username = user.username,
                        verificationLink = verificationLink
                    )
                    return@useTransaction success(true)
                }
                "teacher", "technical_service" -> {
                    val adminEmails = adminRepository.getAllAdminEmails()
                    val approvalLink = "${frontendUrl}approve-request?id=$verificationToken"
                    val userDetails = UserDetails(
                        username = user.username,
                        email = user.email,
                        requestedRole = role.uppercase(Locale.getDefault())
                    )
                    emailService.sendAdminApprovalRequest(adminEmails, approvalLink, userDetails)
                    return@useTransaction success(false)
                }
                else -> return@useTransaction failure(AuthError.InvalidRole)
            }
        }
    }

    fun getAllPendingApprovals(limit:String?, offset:String?): PendingRolesResult {
        val newLimit = if (limit != null) {
            limit.toIntOrNull()?.absoluteValue ?: return failure(AuthError.InvalidLimit)
        } else {
            10
        }
        val newOffset = if (offset != null) {
            offset.toIntOrNull()?.absoluteValue ?: return failure(AuthError.InvalidOffset)
        } else {
            0
        }
        return transactionInterface.useTransaction {
            val approvals = roleApprovalRepository.getApprovals(newLimit, newOffset)
            return@useTransaction success(approvals)
        }
    }

    fun getApprovalByToken(token: String?): PendingRoleResult {
        if(token.isNullOrBlank()) {
            return failure(AuthError.TokenValidationFailed)
        }
        return transactionInterface.useTransaction {
            val roleApproval = roleApprovalRepository.getApprovalByToken(token)
                ?: return@useTransaction failure(AuthError.TokenValidationFailed)
            return@useTransaction success(roleApproval)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun assessRoleRequest(token: String?, adminUserId: Int, status: String?): AssesResult {
        return transactionInterface.useTransaction {
            if(token.isNullOrBlank()) {
                return@useTransaction failure(AuthError.TokenValidationFailed)
            }
            if(status.isNullOrBlank()) {
                return@useTransaction failure(AuthError.TokenValidationFailed)
            }
            val newStatus = when(status) {
                "approved" -> Status.APPROVED
                "rejected" -> Status.REJECTED
                else -> return@useTransaction failure(AuthError.TokenValidationFailed)
            }
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
            roleApproval.status = newStatus
            if(!roleApprovalRepository.updateApproval(roleApproval)) {
                return@useTransaction failure(AuthError.RoleApprovedFailed)
            }
            if(newStatus == Status.REJECTED) {
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
                    return@useTransaction failure(AuthError.InvalidRole)
                }
            }
            return@useTransaction success(true)
        }
    }

    fun login(email: String, password: String): LoginResult {
        if(email.isBlank() || password.isBlank()) {
            return failure(AuthError.MissingCredentials)
        }
        return transactionInterface.useTransaction {
            val user = userRepository.findByEmail(email)
                ?: return@useTransaction failure(AuthError.UserNotFound)
            if(!User.verifyPassword(user.password, password)) {
                return@useTransaction failure(AuthError.InvalidCredentials)
            }
            val role = userRepository.getRoleById(user.id)
                ?: return@useTransaction failure(AuthError.UserNotFound)
            val token = jwtConfig.generateToken(user.id, role.name)
            return@useTransaction success(token)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun verifyStudentAccount(token: String): VerificationResult{
        return transactionInterface.useTransaction {
            val roleApproval = roleApprovalRepository.getApprovalByToken(token)
                ?: return@useTransaction failure(AuthError.TokenValidationFailed)
            if (roleApproval.requestedRole != Role.STUDENT) {
                return@useTransaction failure(AuthError.InvalidRole)
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

    private fun generateVerificationToken(): String {
        return UUID.randomUUID().toString()
    }
}