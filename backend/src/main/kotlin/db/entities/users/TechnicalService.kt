package isel.leic.group25.db.entities.users

import isel.leic.group25.db.entities.types.Role
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface TechnicalService: Entity<TechnicalService> {
    companion object: Entity.Factory<TechnicalService>()
    var user: User
    val role: Role get() = Role.TECHNICAL_SERVICE
}