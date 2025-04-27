package isel.leic.group25.db.tables.issues

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.tables.rooms.Rooms
import isel.leic.group25.db.tables.users.TechnicalServices
import isel.leic.group25.db.tables.users.Users
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

object IssueReports: Table<IssueReport>("issue_report") {
    val id = int("id").primaryKey().bindTo { it.id }
    val room = int("room_id").references(Rooms){ it.room }
    val createdBy = int("created_by").references(Users){it.createdBy}
    val assignedTo = int("assigned_to").references(TechnicalServices){it.assignedTo}
    val description = text("description").bindTo { it.description }
}
