package isel.leic.group25.db.entities.timetables

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface Class: Entity<Class> {
    companion object: Entity.Factory<Class>()
    val id: Int
    var name: String
    var subject: Subject
}