package isel.leic.group25.services

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.db.repositories.interfaces.Transactionable
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import java.sql.SQLException

typealias UserResult = Either<AuthError, User>

typealias TeacherResult = Either<AuthError, Teacher>

typealias RoleResult = Either<AuthError, Role?>

typealias DeleteUserResult = Either<AuthError, Boolean>

class UserService(private val repositories: Repositories,
                  private val transactionable: Transactionable) {
    private inline fun <T> runCatching(block: () -> Either<AuthError, T>): Either<AuthError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(AuthError.ConnectionDbError(e.message))
        }
    }

    fun getUserById(id: Int): UserResult {
        return runCatching {
            val user = repositories.from({userRepository}){
                findById(id)
            } ?: return@runCatching failure(AuthError.UserNotFound)
            return@runCatching success(user)
        }
    }

    fun getTeacherById(id: Int): TeacherResult {
        return runCatching {
            val teacher = repositories.from({teacherRepository}){findTeacherById(id)}
                ?: return@runCatching failure(AuthError.UserNotFound)
            return@runCatching success(teacher)
        }
    }

    fun updateUser(id: Int, username: String?, image: ByteArray?): UserResult {
        return runCatching {
            transactionable.useTransaction {
                val user = repositories.from({userRepository}){findById(id)}
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                if (username != null) {
                    user.username = username
                }
                if (image != null) {
                    user.profileImage = image
                }
                val rowsChanged = repositories.from({userRepository}){update(user)}
                if (rowsChanged == 0) {
                    return@useTransaction failure(AuthError.UserChangesFailed)
                }
                return@useTransaction success(user)
            }
        }
    }

    fun associateUniversity(userId: Int, universityId: Int): UserResult {
        return runCatching {
            transactionable.useTransaction {
                val user = repositories.from({userRepository}){findById(userId)}
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                val university = repositories.from({universityRepository}){getUniversityById(universityId)}
                    ?: return@useTransaction failure(AuthError.UniversityNotFound)
                user.university = university
                val rowsChanged = repositories.from({userRepository}){update(user)}
                if (rowsChanged == 0) {
                    return@useTransaction failure(AuthError.UserChangesFailed)
                }
                return@useTransaction success(user)
            }
        }
    }

    fun changePassword(userId: Int, oldPassword: String, newPassword: String) : UserResult {
        return runCatching {
            transactionable.useTransaction {
                val user = repositories.from({userRepository}){findById(userId)}
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                if (user.authProvider != "local") {
                    return@useTransaction failure(AuthError.LocalAccountRequired)
                }
                if(!User.verifyPassword(user.password, oldPassword)) {
                    return@useTransaction failure(AuthError.InvalidCredentials)
                }
                user.password = User.hashPassword(newPassword)
                val rowsChanged = repositories.from({userRepository}){update(user)}
                if (rowsChanged == 0) {
                    return@useTransaction failure(AuthError.UserChangesFailed)
                }
                return@useTransaction success(user)
            }
        }
    }

    fun getRoleByUserId(userId: Int): RoleResult {
        return runCatching {
            transactionable.useTransaction {
                val role = repositories.from({userRepository}){getRoleById(userId)}
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                return@useTransaction success(role)
            }
        }
    }
    fun deleteUser(id: Int): DeleteUserResult {
        return runCatching {
            transactionable.useTransaction {
                repositories.from({userRepository}){findById(id)}
                    ?: return@useTransaction failure(AuthError.UserNotFound)
                val deleted = repositories.from({userRepository}){delete(id)}
                if (!deleted) {
                    return@useTransaction failure(AuthError.UserDeleteFailed)
                }
                return@useTransaction success(true)
            }
        }
    }
}