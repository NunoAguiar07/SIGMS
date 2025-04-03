package isel.leic.group25.services

import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.tables.Tables.Companion.users
import isel.leic.group25.services.errors.AuthError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.any
import org.ktorm.entity.find

typealias UserResult = Either<AuthError, User>

typealias TokenResult = Either<AuthError, String>

class AuthService(private val database: Database, private val jwtConfig: JwtConfig) {

    fun register(email: String, username: String, password: String): UserResult {
        return database.useTransaction {
            if(email.isBlank() || username.isBlank() || password.isBlank()) {
                return@useTransaction failure(AuthError.MissingCredentials)
            }
            if(User.isNotSecurePassword(password)) {
                return@useTransaction failure(AuthError.InsecurePassword)
            }
            if(database.users.any { it.email eq email }) {
                return@useTransaction failure(AuthError.UserAlreadyExists)
            }

            val newUser = User {
                this.email = email
                this.username = username
                this.password = User.hashPassword(password)
            }

            database.users.add(newUser)
            return@useTransaction success(newUser)
        }
    }

    fun login(email: String, password: String): TokenResult {
        return database.useTransaction {
            if(email.isBlank() || password.isBlank()) {
                return@useTransaction failure(AuthError.MissingCredentials)
            }
            val user = database.users.find { it.email eq email } ?: return@useTransaction failure(AuthError.UserNotFound)
            if(!User.verifyPassword(user.password, password)) {
                return@useTransaction failure(AuthError.InvalidCredentials)
            }
            val token = jwtConfig.generateToken(user.username)
            return@useTransaction success(token)

        }
    }
}