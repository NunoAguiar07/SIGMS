package isel.leic.group25.db.entities.issues

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.users.TechnicalService
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.notifications.interfaces.Notifiable
import isel.leic.group25.notifications.model.ExpoNotification
import isel.leic.group25.notifications.model.NavigateToUrl
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface IssueReport: Entity<IssueReport>, Notifiable {
    companion object: Entity.Factory<IssueReport>()
    val id: Int
    var room: Room
    var createdBy: User
    var assignedTo: TechnicalService?
    var description: String
    override fun toNotification(): ExpoNotification {
        return ExpoNotification("New Issue Report", "Check Issue Reports for more information", NavigateToUrl("/issue-reports"))
    }
}