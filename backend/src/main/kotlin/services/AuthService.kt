package isel.leic.group25.services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.types.Status
import isel.leic.group25.db.entities.users.RoleApproval
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.db.repositories.interfaces.Transactionable
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
    private val repositories: Repositories,
    private val transactionable: Transactionable,
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
            transactionable.useTransaction {
                val user = repositories.from({userRepository}){findByEmail(email)}
                return@useTransaction if (user != null) {
                    if (user.authProvider == "local") {
                        return@useTransaction failure(AuthError.MicrosoftAccountRequired)
                    }
                    val token = jwtConfig.generateToken(user.id, Role.STUDENT.name, user.university.id)
                    success(token)
                } else {
                    val university = repositories.from({universityRepository}){
                        getUniversityByName(universityName)
                    }
                        ?: return@useTransaction failure(AuthError.UniversityNotFound)
                    val newUser = repositories.from({userRepository}){
                        createWithoutRole(email, username, "", university, "microsoft")
                    }
                    repositories.from({userRepository}){associateWithRole(newUser, Role.STUDENT)}
                    val token = jwtConfig.generateToken(newUser.id, Role.STUDENT.name, university.id)
                    success(token)
                }
            }
        }
    }

    fun register(email: String, username: String, password: String, role:Role, universityId: Int): RegisterResult {
        val verificationToken = generateVerificationToken()
        return runCatching {
            transactionable.useTransaction {
                if(repositories.from({userRepository}){findByEmail(email)} != null) {
                    return@useTransaction failure(AuthError.UserAlreadyExists)
                }
                val university = repositories.from({universityRepository}){getUniversityById(universityId)}
                    ?: return@useTransaction failure(AuthError.UniversityNotFound)
                val user = repositories.from({userRepository}){
                    createWithoutRole(email, username, password, university, "local")
                }
                if(!repositories.from({roleApprovalRepository}){
                    addPendingApproval(user, role, verificationToken, null)
                }) {
                    return@useTransaction failure(AuthError.RoleApprovedFailed)
                }
                return@useTransaction when(role) {
                    Role.STUDENT -> {
                        val verificationLink = "${frontendUrl}auth/verify-account?token=$verificationToken"
                        emailService.sendStudentVerificationEmail(
                            userEmail = user.email,
                            username = user.username,
                            verificationLink = verificationLink
                        )
                         success(true)
                    }
                    Role.TEACHER, Role.TECHNICAL_SERVICE -> {
                        val adminEmails = repositories.from({adminRepository}){getAllAdminEmails()}
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
            transactionable.useTransaction {
                val approvals = repositories.from({roleApprovalRepository}){getApprovals(limit, offset)}
                return@useTransaction success(approvals)
            }
        }
    }

    fun getApprovalByToken(token: String): PendingRoleResult {
        return runCatching {
            transactionable.useTransaction {
                val roleApproval = repositories.from({roleApprovalRepository}){getApprovalByToken(token)}
                    ?: return@useTransaction failure(AuthError.TokenValidationFailed)
                return@useTransaction success(roleApproval)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun validateRoleStatus(token: String, adminUserId: Int, status: Status): Either<AuthError, RoleApproval>{
        val roleApproval = repositories.from({roleApprovalRepository}){
            getApprovalByToken(token)
        } ?: return failure(AuthError.TokenValidationFailed)
        if(roleApproval.status != Status.PENDING) {
            return failure(AuthError.AlreadyProcessed)
        }
        if(roleApproval.expiresAt.minus(Clock.System.now()).isNegative()) {
            return failure(AuthError.TokenValidationFailed)
        }
        val admin = repositories.from({adminRepository}){findAdminById(adminUserId)}
            ?: return failure(AuthError.UserNotFound)
        if(!repositories.from({adminRepository}){isAdmin(admin.user)}) {
            return failure(AuthError.UserNotFound)
        }
        roleApproval.verifiedBy = admin
        roleApproval.status = status
        return success(roleApproval)
    }


    fun assessRoleRequest(token: String, adminUserId: Int, status: Status): AssessResult {
        return runCatching {
            transactionable.useTransaction {
                val validation = validateRoleStatus(token, adminUserId, status)
                if(validation is Either.Left){
                    return@useTransaction failure(validation.value)
                }
                val roleApproval = (validation as Either.Right).value
                if(!repositories.from({roleApprovalRepository}){updateApproval(roleApproval)}) {
                    return@useTransaction failure(AuthError.RoleApprovedFailed)
                }
                if(status == Status.REJECTED) {
                    emailService.sendUserApprovalNotification(roleApproval.user.email, false)
                    return@useTransaction success(true)
                }
                val user = repositories.from({userRepository}){findById(roleApproval.user.id)}
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                val newRole = roleApproval.requestedRole
                when(newRole) {
                    Role.TEACHER -> {
                        repositories.from({userRepository}){
                            associateWithRole(user, Role.TEACHER)
                        }
                        emailService.sendUserApprovalNotification(user.email, true)
                    }
                    Role.TECHNICAL_SERVICE -> {
                        repositories.from({userRepository}){
                            associateWithRole(user, Role.TECHNICAL_SERVICE)
                        }
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
            transactionable.useTransaction {
                val user = repositories.from({userRepository}){findByEmail(email)}
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                if (user.authProvider == "microsoft") {
                    return@useTransaction failure(AuthError.MicrosoftLoginRequired)
                }
                if(!User.verifyPassword(user.password, password)) {
                    return@useTransaction failure(AuthError.InvalidCredentials)
                }
                val role = repositories.from({userRepository}){getRoleById(user.id)}
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                val token = jwtConfig.generateToken(user.id, role.name, user.university.id)
                return@useTransaction success(token)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun verifyStudentAccount(token: String): VerificationResult {
        return runCatching {
            transactionable.useTransaction {
                val roleApproval = repositories.from({roleApprovalRepository}){getApprovalByToken(token)}
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
                val user = repositories.from({userRepository}){findById(roleApproval.user.id)}
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                roleApproval.status = Status.APPROVED
                if (!repositories.from({roleApprovalRepository}){updateApproval(roleApproval)}) {
                    return@useTransaction failure(AuthError.RoleApprovedFailed)
                }
                repositories.from({userRepository}){associateWithRole(user, Role.STUDENT)}
                val jwtToken = jwtConfig.generateToken(user.id, Role.STUDENT.name, user.university.id)
                return@useTransaction success(jwtToken)
            }
        }
    }

    private fun generateVerificationToken(): String {
        return UUID.randomUUID().toString()
    }
}