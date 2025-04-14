package isel.leic.group25.db.repositories.issues

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.repositories.issues.interfaces.IssueReportRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.issueReports
import isel.leic.group25.db.tables.Tables.Companion.rooms
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.map
import org.ktorm.entity.removeIf

class IssueReportRepository(private val database: Database) : IssueReportRepositoryInterface{
    override fun getAllIssueReports(): List<IssueReport> {
        return database.issueReports.map { it }
    }

    override fun deleteIssueReport(id: Int): Boolean {
        val issueReport = database.issueReports.firstOrNull { it.id eq  id }
        return if (issueReport != null) {
            database.issueReports.removeIf { it.id eq id }
            true
        } else {
            false
        }
    }

    override fun getIssueReportById(id: Int): IssueReport? {
        return database.issueReports.firstOrNull { it.id eq id }
    }

    override fun createIssueReport(room: Room, description: String): IssueReport? {
        val newIssueReport = IssueReport {
            this.room = room
            this.description = description
        }
        database.issueReports.add(newIssueReport)
        return getIssueReportById(newIssueReport.id)
    }

    override fun updateIssueReport(id: Int, roomId: Int, description: String): IssueReport? {
        return database.issueReports.firstOrNull { it.id eq id }?.let { issueReport ->
            issueReport.room = database.rooms.firstOrNull { it.id eq roomId } ?: return null
            issueReport.description = description
            issueReport
        }
    }

}

