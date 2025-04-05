package isel.leic.group25.db.repositories.users

import isel.leic.group25.db.entities.users.Admin
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.exceptions.users.UserNotInRole
import isel.leic.group25.db.tables.Tables.Companion.admins
import isel.leic.group25.db.tables.Tables.Companion.users
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.any
import org.ktorm.entity.firstOrNull

class AdminRepository(private val database: Database) {
    fun findAdminById(id: Int): Admin? {
       return database.admins.firstOrNull { it.user eq id }
    }

    fun findAdminByEmail(email:String): Admin? {
        val user = database.users.firstOrNull { it.email eq email } ?: return null
        return database.admins.firstOrNull { it.user eq user.id }
    }

    fun isAdmin(user: User): Boolean {
        return database.admins.any { it.user eq user.id }
    }

    fun User.toAdmin(): Admin {
        val admin = database.admins.firstOrNull { it.user eq id } ?: throw UserNotInRole()
        return admin
    }
}