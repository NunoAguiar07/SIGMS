package mocks.repositories.issues

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.repositories.issues.interfaces.IssueReportRepositoryInterface

class MockIssueReportRepository : IssueReportRepositoryInterface {
    private val issueReports = mutableListOf<IssueReport>()

    override fun getAllIssueReports(limit: Int, offset: Int): List<IssueReport> {
        return issueReports.drop(offset).take(limit)
    }

    override fun getIssuesReportByRoomId(roomId: Int, limit: Int, offset: Int): List<IssueReport> {
        return issueReports.filter { it.room.id == roomId }
            .drop(offset)
            .take(limit)
    }

    override fun deleteIssueReport(id: Int): Boolean {
        return issueReports.removeIf { it.id == id }
    }

    override fun getIssueReportById(id: Int): IssueReport? {
        return issueReports.firstOrNull { it.id == id }
    }

    override fun createIssueReport(room: Room, description: String): IssueReport {
        val newIssueReport = IssueReport {
            this.room = room
            this.description = description
        }
        issueReports.add(newIssueReport)
        return newIssueReport
    }

    override fun updateIssueReport(issueReport: IssueReport, description: String): IssueReport {
        val existing = issueReports.firstOrNull { it.id == issueReport.id }
        return existing?.apply {
            this.description = description
        } ?: issueReport
    }

    fun clear() {
        issueReports.clear()
    }
}