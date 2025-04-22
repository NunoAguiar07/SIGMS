package isel.leic.group25.services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.Status
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.entities.users.RoleApproval
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.users.AdminRepository
import isel.leic.group25.db.repositories.users.RoleApprovalRepository
import isel.leic.group25.db.repositories.users.UserRepository
import isel.leic.group25.services.email.EmailService
import isel.leic.group25.services.email.model.UserDetails
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import java.util.*
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

typealias RegisterResult = Either<AuthError, String?>

typealias LoginResult = Either<AuthError, String>

typealias PendingRolesResult = Either<AuthError, List<RoleApproval>>

typealias PendingRoleResult = Either<AuthError, RoleApproval>

typealias AssesResult = Either<AuthError, Boolean>

class AuthService(
    private val userRepository: UserRepository,
    private val adminRepository: AdminRepository,
    private val roleApprovalRepository: RoleApprovalRepository,
    private val transactionInterface: TransactionInterface,
    private val jwtConfig: JwtConfig,
    private val emailService: EmailService,
    private val frontendUrl: String
) {
    private val tokenExpirationDays = 7

    @OptIn(ExperimentalTime::class)
    fun register(email: String, username: String, password: String, role:String): RegisterResult {
        if(email.isBlank() || username.isBlank() || password.isBlank()) {
            return failure(AuthError.MissingCredentials)
        }
        if(User.isNotSecurePassword(password)) {
            return failure(AuthError.InsecurePassword)
        }
        return transactionInterface.useTransaction {
            if(userRepository.findByEmail(email) != null) {
                return@useTransaction failure(AuthError.UserAlreadyExists)
            }
            val newUser = User {
                this.email = email
                this.username = username
                this.password = User.hashPassword(password)
                this.profileImage = ByteArray(0)
            }
            when(role) {
                "student" -> {
                    userRepository.createWithRole(newUser, Role.STUDENT)
                    val token = jwtConfig.generateToken(newUser.id, role.uppercase(Locale.getDefault()))
                    return@useTransaction success(token)
                }
                "teacher", "technical_service" -> {
                    userRepository.createWithoutRole(newUser)
                    val verificationToken = generateVerificationToken()
                    val expiresAt = Clock.System.now() + tokenExpirationDays.days
                    val roleApproval = RoleApproval {
                        this.user = newUser
                        this.requestedRole = Role.valueOf(role.uppercase(Locale.getDefault()))
                        this.verificationToken = verificationToken
                        this.verifiedBy = null
                        this.expiresAt = expiresAt
                        this.status = Status.PENDING
                    }
                    if(!roleApprovalRepository.addPendingApproval(roleApproval)) {
                        return@useTransaction failure(AuthError.RoleApprovedFailed)
                    }
                    val adminEmails = adminRepository.getAllAdminEmails()
                    val approvalLink = "${frontendUrl}approve-request?id=$verificationToken"
                    val userDetails = UserDetails(
                        username = newUser.username,
                        email = newUser.email,
                        requestedRole = role.uppercase(Locale.getDefault())
                    )
                    emailService.sendAdminApprovalRequest(adminEmails, approvalLink, userDetails)
                }
                else -> return@useTransaction failure(AuthError.InvalidRole)
            }
            return@useTransaction success(null)
        }
    }

    fun getAllPendingApprovals(limit:String?, offset:String?): PendingRolesResult {
        val newLimit = if (limit != null) {
            limit.toIntOrNull() ?: return failure(AuthError.InvalidLimit)
        } else {
            10
        }
        val newOffset = if (offset != null) {
            offset.toIntOrNull() ?: return failure(AuthError.InvalidOffset)
        } else {
            0
        }
        return transactionInterface.useTransaction {
            val approvals = roleApprovalRepository.getApprovals(newLimit, newOffset)
            if(approvals.isEmpty()) {
                return@useTransaction success(emptyList())
            }
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
    fun assesRoleRequest(token: String?, adminUserId: Int, status: String?): AssesResult {
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


    private fun generateVerificationToken(): String {
        return UUID.randomUUID().toString()
    }
}