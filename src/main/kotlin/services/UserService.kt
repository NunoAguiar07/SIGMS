package isel.leic.group25.services

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.users.interfaces.UserRepositoryInterface
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import java.sql.SQLException

typealias UserResult = Either<AuthError, User>

typealias RoleResult = Either<AuthError, Role?>

class UserService(private val repository: UserRepositoryInterface,
                  private val transactionInterface: TransactionInterface) {
    private inline fun <T> runCatching(block: () -> Either<AuthError, T>): Either<AuthError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(AuthError.ConnectionDbError(e.message))
        }
    }

    fun getUserById(id: Int): UserResult {
        return runCatching {
            val user = repository.findById(id)
                ?: return@runCatching failure(AuthError.UserNotFound)
            return@runCatching success(user)
        }
    }

    fun updateUser(id: Int, username: String?, image: ByteArray?): UserResult {
        return runCatching {
            transactionInterface.useTransaction {
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
    }

    fun changePassword(userId: Int, oldPassword: String, newPassword: String) : UserResult {
        return runCatching {
            transactionInterface.useTransaction {
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
    }

    fun getRoleByUserId(userId: Int): RoleResult {
        return runCatching {
            transactionInterface.useTransaction {
                val role = repository.getRoleById(userId)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                return@useTransaction success(role)
            }
        }
    }
    fun deleteUser(id: Int): UserResult {
        return runCatching {
            transactionInterface.useTransaction {
                val user = repository.findById(id)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                val deleted = repository.delete(id)
                if (!deleted) {
                    return@useTransaction failure(AuthError.UserDeleteFailed)
                }
                return@useTransaction success(user)
            }
        }
    }
}