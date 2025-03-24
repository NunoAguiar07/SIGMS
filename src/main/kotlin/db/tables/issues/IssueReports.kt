package isel.leic.group25.db.tables.issues

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.tables.rooms.Rooms
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

object IssueReports: Table<IssueReport>("ISSUE_REPORT") {
    val id = int("id").primaryKey()
    val room = int("room_id").references(Rooms){ it.room }
    val description = text("description").bindTo { it.description }
}