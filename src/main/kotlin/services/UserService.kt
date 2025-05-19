package isel.leic.group25.services

import UniversityRepositoryInterface
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

typealias DeleteUserResult = Either<AuthError, Boolean>

class UserService(private val userRepository: UserRepositoryInterface,
                  private val universityRepository: UniversityRepositoryInterface,
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
            val user = userRepository.findById(id)
                ?: return@runCatching failure(AuthError.UserNotFound)
            return@runCatching success(user)
        }
    }

    fun updateUser(id: Int, username: String?, image: ByteArray?): UserResult {
        return runCatching {
            transactionInterface.useTransaction {
                val user = userRepository.findById(id)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                if (username != null) {
                    user.username = username
                }
                if (image != null) {
                    user.profileImage = image
                }
                val rowsChanged = userRepository.update(user)
                if (rowsChanged == 0) {
                    return@useTransaction failure(AuthError.UserChangesFailed)
                }
                return@useTransaction success(user)
            }
        }
    }

    fun associateUniversity(userId: Int, universityId: Int): UserResult {
        return runCatching {
            transactionInterface.useTransaction {
                val user = userRepository.findById(userId)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                val university = universityRepository.getUniversityById(universityId)
                    ?: return@useTransaction failure(AuthError.UniversityNotFound)
                user.university = university
                val rowsChanged = userRepository.update(user)
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
                val user = userRepository.findById(userId)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                if (user.authProvider != "local") {
                    return@useTransaction failure(AuthError.LocalAccountRequired)
                }
                if(!User.verifyPassword(user.password, oldPassword)) {
                    return@useTransaction failure(AuthError.InvalidCredentials)
                }
                user.password = User.hashPassword(newPassword)
                val rowsChanged = userRepository.update(user)
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
                val role = userRepository.getRoleById(userId)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                return@useTransaction success(role)
            }
        }
    }
    fun deleteUser(id: Int): DeleteUserResult {
        return runCatching {
            transactionInterface.useTransaction {
                userRepository.findById(id)
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                val deleted = userRepository.delete(id)
                if (!deleted) {
                    return@useTransaction failure(AuthError.UserDeleteFailed)
                }
                return@useTransaction success(true)
            }
        }
    }
}