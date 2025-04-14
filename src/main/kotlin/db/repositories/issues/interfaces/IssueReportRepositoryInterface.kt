package isel.leic.group25.db.repositories.issues.interfaces

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Room


interface IssueReportRepositoryInterface {
    fun getAllIssueReports(): List<IssueReport>
    fun getIssueReportById(id: Int): IssueReport?
    fun createIssueReport(room: Room, description: String): IssueReport?
    fun deleteIssueReport(id: Int): Boolean
    fun updateIssueReport(id: Int, roomId: Int, description: String): IssueReport?
}