package isel.leic.group25.db.repositories.users

import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.*
import isel.leic.group25.db.repositories.users.interfaces.UserRepositoryInterface
import isel.leic.group25.db.repositories.utils.withDatabase
import isel.leic.group25.db.tables.Tables.Companion.admins
import isel.leic.group25.db.tables.Tables.Companion.students
import isel.leic.group25.db.tables.Tables.Companion.teachers
import isel.leic.group25.db.tables.Tables.Companion.technicalServices
import isel.leic.group25.db.tables.Tables.Companion.users
import isel.leic.group25.db.tables.users.Users
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.update
import org.ktorm.entity.*

class UserRepository(private val database: Database): UserRepositoryInterface {
    override fun findById(id: Int): User? = withDatabase {
        return database.users.firstOrNull { it.id eq id }
    }

    override fun findByEmail(email:String): User? = withDatabase {
        return database.users.firstOrNull { it.email eq email }
    }

    override fun associateWithRole(newUser: User, role: Role): User = withDatabase {
        when(role){
            Role.ADMIN -> {
                database.admins.add(Admin{user=newUser})
            }
            Role.STUDENT -> {
                database.students.add(Student{user=newUser})
            }
            Role.TEACHER -> {
                database.teachers.add(Teacher{user=newUser})
            }
            Role.TECHNICAL_SERVICE -> {
                database.technicalServices.add(TechnicalService{user=newUser})
            }
        }
        return newUser
    }

    override fun createWithRole(email: String, username: String, password: String, role: Role, university: University, authProvider: String): User = withDatabase {
        val newUser = User {
            this.email = email
            this.username = username
            this.password = User.hashPassword(password)
            this.authProvider = authProvider
            this.university = university
        }
        database.users.add(newUser)
        return associateWithRole(newUser, role)
    }

    override fun createWithoutRole(email: String, username: String, password: String, university: University, authProvider: String): User = withDatabase {
        val newUser = User {
            this.email = email
            this.username = username
            this.password = User.hashPassword(password)
            this.authProvider = authProvider
            this.university = university
        }
        database.users.add(newUser)
        return newUser
    }

    override fun update(user: User): Int =
        withDatabase {
        database.update(Users) {
            set(it.username, user.username)
            set(it.profileImage, user.profileImage)
            where { it.id eq user.id }
        }
    }

    override fun getRoleById(id: Int): Role? = withDatabase {
        return when {
            database.admins.any { it.user eq id } -> Role.ADMIN
            database.students.any { it.user eq id } -> Role.STUDENT
            database.teachers.any { it.user eq id } -> Role.TEACHER
            database.technicalServices.any { it.user eq id } -> Role.TECHNICAL_SERVICE
            else -> null
        }
    }

    override fun delete(id: Int): Boolean = withDatabase {
        database.users.firstOrNull { it.id eq id } ?: return false
        database.useTransaction {
            database.admins.removeIf { it.user eq id }
            database.students.removeIf { it.user eq id }
            database.teachers.removeIf { it.user eq id }
            database.technicalServices.removeIf { it.user eq id }
            database.users.removeIf { it.id eq id }
        }
        return true
    }
}