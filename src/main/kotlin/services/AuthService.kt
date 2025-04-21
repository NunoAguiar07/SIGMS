package isel.leic.group25.services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.users.UserRepository
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import java.util.*

class AuthService(
    private val repository: UserRepository,
    private val transactionInterface: TransactionInterface,
    private val jwtConfig: JwtConfig
) {
    fun register(email: String, username: String, password: String, role:String): TokenResult {
        if(email.isBlank() || username.isBlank() || password.isBlank()) {
            return failure(AuthError.MissingCredentials)
        }
        if(User.isNotSecurePassword(password)) {
            return failure(AuthError.InsecurePassword)
        }
        return transactionInterface.useTransaction {
            if(repository.findByEmail(email) != null) {
                return@useTransaction failure(AuthError.UserAlreadyExists)
            }
            val newUser = User {
                this.email = email
                this.username = username
                this.password = User.hashPassword(password)
                this.profileImage = ByteArray(0)
            }
            when(role) {
                "teacher" -> repository.create(newUser, Role.TEACHER)
                "student" -> repository.create(newUser, Role.STUDENT)
                "technician" -> repository.create(newUser, Role.TECHNICAL_SERVICE)
                else -> return@useTransaction failure(AuthError.InvalidRole)
            }
            val token = jwtConfig.generateToken(newUser.id, role.uppercase(Locale.getDefault()))
            return@useTransaction success(token)
        }
    }

    fun login(email: String, password: String): TokenResult {
        if(email.isBlank() || password.isBlank()) {
            return failure(AuthError.MissingCredentials)
        }
        return transactionInterface.useTransaction {
            val user = repository.findByEmail(email)
                ?: return@useTransaction failure(AuthError.UserNotFound)
            if(!User.verifyPassword(user.password, password)) {
                return@useTransaction failure(AuthError.InvalidCredentials)
            }
            val role = repository.getRoleById(user.id)
                ?: return@useTransaction failure(AuthError.UserNotFound)
            val token = jwtConfig.generateToken(user.id, role.name)
            return@useTransaction success(token)
        }
    }
}