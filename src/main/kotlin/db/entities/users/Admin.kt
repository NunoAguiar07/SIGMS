package isel.leic.group25.db.entities.users

import isel.leic.group25.db.entities.types.Role
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface Admin: Entity<Admin> {
    companion object: Entity.Factory<Admin>()
    var user: User
    val role: Role get() = Role.ADMIN


}