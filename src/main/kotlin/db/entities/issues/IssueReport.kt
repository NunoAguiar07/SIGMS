package isel.leic.group25.db.entities.issues

import isel.leic.group25.db.entities.rooms.Room
import org.ktorm.entity.Entity

interface IssueReport: Entity<IssueReport> {
    val id: Int
    var room: Room
    val description: String
}