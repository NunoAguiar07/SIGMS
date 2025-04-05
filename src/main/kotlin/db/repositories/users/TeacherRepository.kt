package isel.leic.group25.db.repositories.users

import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.exceptions.users.UserNotInRole
import isel.leic.group25.db.tables.Tables.Companion.teachers
import isel.leic.group25.db.tables.Tables.Companion.users
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.any
import org.ktorm.entity.firstOrNull

class TeacherRepository(private val database: Database) {
    fun findStudentById(id: Int): Teacher? {
        return database.teachers.firstOrNull { it.user eq id }
    }

    fun findStudentByEmail(email:String): Teacher?{
        val user = database.users.firstOrNull { it.email eq email } ?: return null
        return database.teachers.firstOrNull { it.user eq user.id }
    }

    fun isTeacher(user: User): Boolean {
        return database.teachers.any { it.user eq user.id }
    }

    fun User.toTeacher(): Teacher {
        val teacher = database.teachers.firstOrNull { it.user eq id } ?: throw UserNotInRole()
        return teacher
    }
}