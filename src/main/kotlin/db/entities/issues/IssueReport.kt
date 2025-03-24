package isel.leic.group25.db.entities.issues

import isel.leic.group25.db.entities.rooms.Room
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface IssueReport: Entity<IssueReport> {
    val id: Int
    var room: Room
    val description: String
}