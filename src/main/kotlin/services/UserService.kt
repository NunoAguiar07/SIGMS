package isel.leic.group25.services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.users.UserRepository
import isel.leic.group25.db.tables.Tables.Companion.users
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.any

typealias UserResult = Either<AuthError, User>

typealias TokenResult = Either<AuthError, String>

class UserService(private val repository: UserRepository,
                  private val database: Database,
                  private val jwtConfig: JwtConfig) {

    fun register(email: String, username: String, password: String): UserResult {
        if(email.isBlank() || username.isBlank() || password.isBlank()) {
            return failure(AuthError.MissingCredentials)
        }
        if(User.isNotSecurePassword(password)) {
            return failure(AuthError.InsecurePassword)
        }
        return database.useTransaction {
            if(database.users.any { it.email eq email }) {
                return@useTransaction failure(AuthError.UserAlreadyExists)
            }
            val newUser = User {
                this.email = email
                this.username = username
                this.password = User.hashPassword(password)
                this.profileImage = ByteArray(0)
            }
            repository.create(newUser, Role.STUDENT)
            return@useTransaction success(newUser)
        }
    }

    fun login(email: String, password: String): TokenResult {
        if(email.isBlank() || password.isBlank()) {
            return failure(AuthError.MissingCredentials)
        }
        return database.useTransaction {
            val user = repository.findByEmail(email)
                ?: return@useTransaction failure(AuthError.UserNotFound)
            if(!User.verifyPassword(user.password, password)) {
                return@useTransaction failure(AuthError.InvalidCredentials)
            }
            val token = jwtConfig.generateToken(user.id.toString())
            return@useTransaction success(token)
        }
    }
}