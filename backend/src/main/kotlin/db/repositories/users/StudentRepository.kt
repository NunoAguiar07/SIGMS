package isel.leic.group25.db.repositories.users

import isel.leic.group25.db.entities.users.Student
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.users.interfaces.StudentRepositoryInterface
import isel.leic.group25.db.repositories.utils.withDatabase
import isel.leic.group25.db.tables.Tables.Companion.students
import isel.leic.group25.db.tables.Tables.Companion.users
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.any
import org.ktorm.entity.firstOrNull

class StudentRepository(private val database: Database): StudentRepositoryInterface {
    override fun findStudentById(id: Int): Student? = withDatabase {
        return database.students.firstOrNull { it.user eq id }
    }

    override fun findStudentByEmail(email:String): Student? = withDatabase {
        val user = database.users.firstOrNull { it.email eq email } ?: return null
        return database.students.firstOrNull { it.user eq user.id }
    }

    override fun isStudent(user: User): Boolean = withDatabase {
        return database.students.any { it.user eq user.id }
    }
}