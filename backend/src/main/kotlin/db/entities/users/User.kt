package isel.leic.group25.db.entities.users

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.tables.Tables.Companion.admins
import isel.leic.group25.db.tables.Tables.Companion.students
import isel.leic.group25.db.tables.Tables.Companion.teachers
import isel.leic.group25.db.tables.Tables.Companion.technicalServices
import kotlinx.serialization.Serializable
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.Entity
import org.ktorm.entity.first

@Serializable
sealed interface User: Entity<User> {
    val id: Int
    var email: String
    var username: String
    var password: String
    var profileImage: ByteArray?
    var authProvider: String
    var university: University

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
                argon2.hash(10, 65536, 1, password.toCharArray())
            } finally {
                argon2.wipeArray(password.toCharArray())
            }
        }

        fun verifyPassword(hash: String, password: String): Boolean {
            return argon2.verify(hash, password.toCharArray())
        }

    }
    fun toAdmin(database: Database): Admin = database.admins.first{ it.user eq id}
    fun toStudent(database: Database): Student = database.students.first{ it.user eq id}
    fun toTeacher(database: Database): Teacher = database.teachers.first{ it.user eq id}
    fun toTechnicalService(database: Database): TechnicalService = database.technicalServices.first{ it.user eq id}
}