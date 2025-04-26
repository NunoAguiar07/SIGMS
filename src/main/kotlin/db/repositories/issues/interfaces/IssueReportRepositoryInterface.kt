package isel.leic.group25.db.repositories.issues.interfaces

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.users.TechnicalService
import isel.leic.group25.db.entities.users.User


interface IssueReportRepositoryInterface {
    fun getAllIssueReports(limit:Int, offset:Int): List<IssueReport>
    fun getIssueReportById(id: Int): IssueReport?
    fun getIssuesReportByRoomId(roomId: Int, limit:Int, offset:Int): List<IssueReport>
    fun createIssueReport(user: User, room: Room, description: String): IssueReport
    fun assignIssueTo(issueReport: IssueReport, technician: TechnicalService) : IssueReport
    fun deleteIssueReport(id: Int): Boolean
    fun updateIssueReport(issueReport: IssueReport, description: String): IssueReport
}