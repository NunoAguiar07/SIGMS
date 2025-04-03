package isel.leic.group25.db.entities.users

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface User: Entity<User> {
    val id: Int
    var email: String
    var username: String
    var password: String
    var profileImage: ByteArray

    companion object: Entity.Factory<User>() {

        private val argon2: Argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

        fun isNotSecurePassword(password: String): Boolean {
            if (password.length < 8) return true
            if (!password.any { it.isDigit() }) return true
            val specialChars = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/`~"
            return !password.any { it in specialChars }
        }

        fun hashPassword(password: String): String {
            return try {
                // need to review the values at a later date
                argon2.hash(10, 65536, 1, password.toCharArray())
            } finally {
                // Wipe sensitive data from memory
                argon2.wipeArray(password.toCharArray())
            }
        }

        fun verifyPassword(hash: String, password: String): Boolean {
            return argon2.verify(hash, password.toCharArray())
        }

    }
}