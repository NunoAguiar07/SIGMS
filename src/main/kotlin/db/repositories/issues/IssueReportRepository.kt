package isel.leic.group25.db.repositories.issues

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.users.TechnicalService
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.issues.interfaces.IssueReportRepositoryInterface
import isel.leic.group25.db.repositories.utils.withDatabase
import isel.leic.group25.db.tables.Tables.Companion.issueReports
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*

class IssueReportRepository(private val database: Database) : IssueReportRepositoryInterface{
    override fun getAllIssueReports(limit:Int, offset:Int): List<IssueReport> = withDatabase {
        return database.issueReports.drop(offset).take(limit).toList()
    }

    override fun getIssuesReportByRoomId(roomId: Int, limit:Int, offset:Int): List<IssueReport> = withDatabase {
        return database.issueReports.filter { it.room eq roomId }
            .drop(offset)
            .take(limit)
            .toList()
    }

    override fun deleteIssueReport(id: Int): Boolean = withDatabase {
        val issueReport = database.issueReports.firstOrNull { it.id eq  id }
        return if (issueReport != null) {
            database.issueReports.removeIf { it.id eq id }
            true
        } else {
            false
        }
    }

    override fun getIssueReportById(id: Int): IssueReport? = withDatabase {
        return database.issueReports.firstOrNull { it.id eq id }
    }

    override fun assignIssueTo(issueReport: IssueReport, technician: TechnicalService) : IssueReport = withDatabase {
        issueReport.assignedTo = technician
        database.issueReports.update(issueReport)
        return issueReport
    }

    override fun createIssueReport(user: User, room: Room, description: String): IssueReport = withDatabase {
        val newIssueReport = IssueReport {
            this.room = room
            this.description = description
            this.createdBy = user
        }
        database.issueReports.add(newIssueReport)
        return newIssueReport
    }

    override fun updateIssueReport(issueReport: IssueReport, description: String): IssueReport = withDatabase {
        issueReport.description = description
        database.issueReports.update(issueReport)
        return issueReport
    }

}

