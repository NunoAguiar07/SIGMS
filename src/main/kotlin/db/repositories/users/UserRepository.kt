package isel.leic.group25.db.repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.*
import isel.leic.group25.db.repositories.users.interfaces.UserRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.admins
import isel.leic.group25.db.tables.Tables.Companion.students
import isel.leic.group25.db.tables.Tables.Companion.teachers
import isel.leic.group25.db.tables.Tables.Companion.technicalServices
import isel.leic.group25.db.tables.Tables.Companion.users
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.any
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.update

class UserRepository(private val database: Database): UserRepositoryInterface {
    override fun findById(id: Int): User? {
        return database.users.firstOrNull { it.id eq id }
    }

    override fun findByEmail(email:String): User? {
        return database.users.firstOrNull { it.email eq email }
    }

    override fun associateWithRole(newUser: User, role: Role): User {
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

    override fun createWithRole(newUser: User, role: Role): User {
        database.users.add(newUser)
        return associateWithRole(newUser, role)
    }

    override fun createWithoutRole(newUser: User): User {
        database.users.add(newUser)
        return newUser
    }

    override fun update(user: User): Int {
        return database.users.update(user)
    }

    override fun getRoleById(id: Int): Role? {
        return when {
            database.admins.any { it.user eq id } -> Role.ADMIN
            database.students.any { it.user eq id } -> Role.STUDENT
            database.teachers.any { it.user eq id } -> Role.TEACHER
            database.technicalServices.any { it.user eq id } -> Role.TECHNICAL_SERVICE
            else -> null
        }
    }
}