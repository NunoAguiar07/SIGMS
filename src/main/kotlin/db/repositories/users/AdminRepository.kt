package isel.leic.group25.db.repositories.users

import isel.leic.group25.db.entities.users.Admin
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.users.interfaces.AdminRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.admins
import isel.leic.group25.db.tables.Tables.Companion.users
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.any
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.mapNotNull

class AdminRepository(private val database: Database): AdminRepositoryInterface {
    override fun getAllAdminEmails(): List<String> {
        return database.admins.mapNotNull { it.user.email }
    }

    override fun findAdminById(id: Int): Admin? {
       return database.admins.firstOrNull { it.user eq id }
    }

    override fun findAdminByEmail(email:String): Admin? {
        val user = database.users.firstOrNull { it.email eq email } ?: return null
        return database.admins.firstOrNull { it.user eq user.id }
    }

    override fun isAdmin(user: User): Boolean {
        return database.admins.any { it.user eq user.id }
    }
}