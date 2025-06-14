package isel.leic.group25.db.entities.users


import isel.leic.group25.db.entities.types.Role
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface Student: Entity<Student> {
    companion object: Entity.Factory<Student>()
    var user: User
    val role: Role get() = Role.STUDENT
}