package isel.leic.group25.db.repositories.users

import isel.leic.group25.db.entities.users.TechnicalService
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.exceptions.users.UserNotInRole
import isel.leic.group25.db.repositories.users.interfaces.TeacherRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.TechnicalServiceRepository
import isel.leic.group25.db.tables.Tables.Companion.technicalServices
import isel.leic.group25.db.tables.Tables.Companion.users
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.any
import org.ktorm.entity.firstOrNull

class TechnicalServiceRepository(private val database: Database): TechnicalServiceRepository {
    override fun findTechnicalServiceById(id: Int): TechnicalService? {
        return database.technicalServices.firstOrNull { it.user eq id }
    }

    override fun findTechnicalServiceByEmail(email:String): TechnicalService?{
        val user = database.users.firstOrNull { it.email eq email } ?: return null
        return database.technicalServices.firstOrNull { it.user eq user.id }
    }

    override fun isTechnicalService(user: User): Boolean {
        return database.technicalServices.any { it.user eq user.id }
    }

    override fun User.toTechnicalService(): TechnicalService {
        val technicalService = database.technicalServices.firstOrNull { it.user eq id } ?: throw UserNotInRole()
        return technicalService
    }
}