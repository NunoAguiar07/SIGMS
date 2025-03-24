package isel.leic.group25.api.model

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory



sealed class User(
    open val id: Int,
    open val email: String,
    open val name: String,
    open val password: String,
    open val profileImage: String?
)  {
    companion object {

        private val argon2: Argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)

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




