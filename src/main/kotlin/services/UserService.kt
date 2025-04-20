package isel.leic.group25.services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.users.UserRepository
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success

typealias UserResult = Either<AuthError, User>

typealias TokenResult = Either<AuthError, String>

typealias RoleResult = Either<AuthError, Role>

class UserService(private val repository: UserRepository,
                  private val transactionInterface: TransactionInterface,
                  private val jwtConfig: JwtConfig) {

    fun getUserById(id: Int): UserResult {
        return transactionInterface.useTransaction {
            val user = repository.findById(id)
                ?: return@useTransaction failure(AuthError.UserNotFound)
            return@useTransaction success(user)
        }
    }

    fun updateUser(id: Int, username: String?, image: ByteArray?): UserResult {
        return transactionInterface.useTransaction {
            val user = repository.findById(id)
                ?: return@useTransaction failure(AuthError.UserNotFound)
            if (username != null) {
                user.username = username
            }
            if (image != null) {
                user.profileImage = image
            }
            val rowsChanged = repository.update(user)
            if (rowsChanged == 0) {
                return@useTransaction failure(AuthError.UserChangesFailed)
            }
            return@useTransaction success(user)
        }
    }

    fun changePassword(userId: Int, oldPassword: String, newPassword: String) : UserResult {
        if(oldPassword.isBlank() || newPassword.isBlank()) {
            return failure(AuthError.MissingCredentials)
        }
        if(User.isNotSecurePassword(newPassword)) {
            return failure(AuthError.InsecurePassword)
        }
        return transactionInterface.useTransaction {
            val user = repository.findById(userId)
                ?: return@useTransaction failure(AuthError.UserNotFound)
            if(!User.verifyPassword(user.password, oldPassword)) {
                return@useTransaction failure(AuthError.InvalidCredentials)
            }
            user.password = User.hashPassword(newPassword)
            val rowsChanged = repository.update(user)
            if (rowsChanged == 0) {
                return@useTransaction failure(AuthError.UserChangesFailed)
            }
            return@useTransaction success(user)
        }
    }

    fun getRoleByUserId(userId: Int): RoleResult {
        return transactionInterface.useTransaction {
            val role = repository.getRoleById(userId)
                ?: return@useTransaction failure(AuthError.UserNotFound)
            return@useTransaction success(role)
        }
    }
}