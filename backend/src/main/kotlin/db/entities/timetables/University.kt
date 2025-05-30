package isel.leic.group25.db.entities.timetables

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface University: Entity<University> {
    companion object: Entity.Factory<University>()
    val id: Int
    var name: String
}
