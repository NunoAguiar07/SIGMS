package isel.leic.group25.db.repositories.users.interfaces

import isel.leic.group25.db.entities.users.TechnicalService
import isel.leic.group25.db.entities.users.User

interface TechnicalServiceRepository {
    fun findTechnicalServiceById(id: Int): TechnicalService?

    fun findTechnicalServiceByEmail(email: String): TechnicalService?

    fun isTechnicalService(user: User): Boolean

    fun User.toTechnicalService(): TechnicalService
}