package isel.leic.group25.db.repositories.users

import isel.leic.group25.db.entities.users.TechnicalService
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.users.interfaces.TechnicalServiceRepositoryInterface
import isel.leic.group25.db.repositories.utils.withDatabase
import isel.leic.group25.db.tables.Tables.Companion.technicalServices
import isel.leic.group25.db.tables.Tables.Companion.users
import isel.leic.group25.db.tables.users.TechnicalServices
import isel.leic.group25.db.tables.users.Users
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.innerJoin
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.entity.any
import org.ktorm.entity.first
import org.ktorm.entity.firstOrNull

class TechnicalServiceRepository(private val database: Database): TechnicalServiceRepositoryInterface {
    override fun findTechnicalServiceById(id: Int): TechnicalService? = withDatabase {
        return database.technicalServices.firstOrNull { it.user eq id }
    }

    override fun findTechnicalServiceByEmail(email:String): TechnicalService? = withDatabase {
        val user = database.users.firstOrNull { it.email eq email } ?: return null
        return database.technicalServices.firstOrNull { it.user eq user.id }
    }

    override fun isTechnicalService(user: User): Boolean = withDatabase {
        return database.technicalServices.any { it.user eq user.id }
    }

    override fun universityTechnicalServices(universityId: Int): List<TechnicalService> {
        return database.from(TechnicalServices)
            .innerJoin(Users, on = TechnicalServices.user eq Users.id)
            .select(TechnicalServices.columns)
            .where { Users.university eq universityId }
            .map { row ->
                TechnicalService{
                    user = database.users.first{ it.id eq row[TechnicalServices.user]!!}
                }
            }
    }
}