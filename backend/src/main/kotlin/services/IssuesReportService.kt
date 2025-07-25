package isel.leic.group25.services

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.db.repositories.interfaces.IsolationLevel
import isel.leic.group25.db.repositories.interfaces.Transactionable
import isel.leic.group25.services.errors.IssueReportError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import isel.leic.group25.notifications.websocket.route.Notifications
import java.sql.SQLException

typealias IssueReportListResult = Either<IssueReportError, List<IssueReport>>

typealias IssueReportResult = Either<IssueReportError, IssueReport>
typealias IssueReportUpdateResult = Either<IssueReportError, IssueReport>

typealias IssueReportDeletionResult = Either<IssueReportError, Boolean>

class IssuesReportService(private val repositories: Repositories,
                          private val transactionable: Transactionable
) {
    private inline fun <T> runCatching(block: () -> Either<IssueReportError, T>): Either<IssueReportError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(IssueReportError.ConnectionDbError(e.message))
        }
    }

    fun getAllIssueReports(limit:Int, offset:Int, unassigned: Boolean): IssueReportListResult {
        return runCatching {
            transactionable.useTransaction {
                val issues = repositories.from({issueReportRepository}){
                    if (unassigned) getAllUnassignedIssueReports(limit, offset)
                    else getAllIssueReports(limit, offset)
                }
                return@useTransaction success(issues)
            }
        }
    }

    fun getIssueReportById(id: Int): IssueReportResult {
        return runCatching {
            transactionable.useTransaction {
                val issue = repositories.from({issueReportRepository}){getIssueReportById(id)}
                    ?: return@useTransaction failure(IssueReportError.IssueReportNotFound)
                return@useTransaction success(issue)
            }
        }
    }

    fun getIssueReportsByUserId(userId: Int, limit:Int, offset:Int): IssueReportListResult {
        return runCatching {
            transactionable.useTransaction {
                val issues = repositories.from({issueReportRepository}){
                    getIssueReportsByUserId(userId, limit, offset)
                }
                return@useTransaction success(issues)
            }
        }
    }

    fun getIssuesReportByRoomId(roomId: Int, limit:Int, offset:Int): IssueReportListResult {
        return runCatching {
            transactionable.useTransaction {
                val issues = repositories.from({issueReportRepository}){
                    getIssuesReportByRoomId(roomId, limit, offset)
                }
                return@useTransaction success(issues)
            }
        }
    }

    fun createIssueReport(userId: Int, roomId: Int, description: String) :IssueReportResult {
        return runCatching {
            transactionable.useTransaction {
                val room = repositories.from({roomRepository}){getRoomById(roomId)}
                    ?: return@useTransaction failure(IssueReportError.InvalidRoomId)
                val user = repositories.from({userRepository}){
                    findById(userId)
                }?: return@useTransaction failure(IssueReportError.UserNotFound)
                val newIssue = repositories.from({issueReportRepository}){
                    createIssueReport(user, room, description)
                }
                val technicians = repositories.from({technicalServiceRepository}){universityTechnicalServices(user.university.id)}
                Notifications.notify { Pair(technicians.map { it.user.id }, newIssue) }
                return@useTransaction success(newIssue)
            }
        }
    }

    fun assignTechnicianToIssueReport(technicianId: Int, reportId: Int) : IssueReportResult{
        return runCatching {
            transactionable.useTransaction {
                val issueReport = repositories.from({issueReportRepository}){
                    getIssueReportById(reportId)
                } ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
                val technician = repositories.from({technicalServiceRepository}){
                    findTechnicalServiceById(technicianId)

                } ?: return@useTransaction failure(IssueReportError.UserNotFound)
                val updatedIssueReport = repositories.from({issueReportRepository}){
                    assignIssueTo(issueReport, technician)
                }
                return@useTransaction success(updatedIssueReport)
            }
        }
    }

    fun unassignTechnicianFromIssueReport(technicianId: Int, reportId: Int): IssueReportResult {
        return runCatching {
            transactionable.useTransaction {
                val issueReport = repositories.from({issueReportRepository}){
                    getIssueReportById(reportId)
                } ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
                val assignedTechnicianId = issueReport.assignedTo?.user?.id ?: 0
                if (issueReport.assignedTo == null || assignedTechnicianId == 0
                    || assignedTechnicianId != technicianId) {
                    return@useTransaction failure(IssueReportError.NotAssignedToIssueReport)
                }
                val unassignedIssue = repositories.from({issueReportRepository}){
                    unassignTechnicianFromIssueReport(issueReport)
                }
                return@useTransaction success(unassignedIssue)
            }
        }
    }

    fun deleteIssueReport(id: Int): IssueReportDeletionResult {
        return runCatching {
            transactionable.useTransaction(IsolationLevel.SERIALIZABLE) {
                repositories.from({issueReportRepository}){
                    getIssueReportById(id)
                } ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
                val deletedIssue = repositories.from({issueReportRepository}){deleteIssueReport(id)}
                return@useTransaction success(deletedIssue)
            }
        }
    }

    fun updateIssueReport(id: Int, description: String): IssueReportUpdateResult {
        return runCatching {
            transactionable.useTransaction(IsolationLevel.SERIALIZABLE) {
                val report = repositories.from({issueReportRepository}){getIssueReportById(id)}
                    ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
                val updatedIssue = repositories.from({issueReportRepository}){
                    updateIssueReport(report, description)
                }
                return@useTransaction success(updatedIssue)
            }
        }
    }

}