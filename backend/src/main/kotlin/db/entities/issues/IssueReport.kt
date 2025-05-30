package isel.leic.group25.db.entities.issues

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.users.TechnicalService
import isel.leic.group25.db.entities.users.User
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface IssueReport: Entity<IssueReport> {
    companion object: Entity.Factory<IssueReport>()
    val id: Int
    var room: Room
    var createdBy: User
    var assignedTo: TechnicalService?
    var description: String
}