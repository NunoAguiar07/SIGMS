package isel.leic.group25.db.entities.users

import isel.leic.group25.db.entities.rooms.OfficeRoom
import isel.leic.group25.db.entities.types.Role
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface Teacher: Entity<Teacher> {
    companion object: Entity.Factory<Teacher>()
    var user: User
    var office: OfficeRoom?
    val role: Role get() = Role.TEACHER
}