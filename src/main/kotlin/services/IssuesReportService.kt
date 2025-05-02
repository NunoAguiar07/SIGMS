package isel.leic.group25.services

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.issues.interfaces.IssueReportRepositoryInterface
import isel.leic.group25.db.repositories.rooms.interfaces.RoomRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.TechnicalServiceRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.UserRepositoryInterface
import isel.leic.group25.services.errors.IssueReportError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success

typealias IssueReportListResult = Either<IssueReportError, List<IssueReport>>

typealias IssueReportResult = Either<IssueReportError, IssueReport>
typealias IssueReportUpdateResult = Either<IssueReportError, IssueReport>

typealias IssueReportDeletionResult = Either<IssueReportError, Boolean>

class IssuesReportService(private val issueReportRepository: IssueReportRepositoryInterface,
                          private val userRepository: UserRepositoryInterface,
                          private val technicalServiceRepository: TechnicalServiceRepositoryInterface,
                          private val transactionInterface: TransactionInterface,
                          private val roomRepository: RoomRepositoryInterface
) {
    fun getAllIssueReports(limit:Int, offset:Int): IssueReportListResult {
        return transactionInterface.useTransaction {
            val issues = issueReportRepository.getAllIssueReports(limit, offset)
           return@useTransaction success(issues)
        }
    }

    fun getIssueReportById(id: Int): IssueReportResult {
        return transactionInterface.useTransaction {
           val issue = issueReportRepository.getIssueReportById(id)
               ?: return@useTransaction failure(IssueReportError.IssueReportNotFound)
            return@useTransaction success(issue)
        }
    }

    fun getIssuesReportByRoomId(roomId: Int, limit:Int, offset:Int): IssueReportListResult {
        return transactionInterface.useTransaction {
            val issues = issueReportRepository.getIssuesReportByRoomId(roomId, limit, offset)
            return@useTransaction success(issues)
        }
    }

    fun createIssueReport(userId: Int, roomId: Int, description: String):IssueReportResult {
        return transactionInterface.useTransaction {
            val room = roomRepository.getRoomById(roomId)
                ?: return@useTransaction failure(IssueReportError.InvalidRoomId)
            val user = userRepository.findById(userId) ?: return@useTransaction failure(IssueReportError.UserNotFound)
            val newIssue = issueReportRepository.createIssueReport(user, room, description)
            return@useTransaction success(newIssue)
        }
    }

    fun assignTechnicianToIssueReport(technicianId: Int, reportId: Int) : IssueReportResult{
        return transactionInterface.useTransaction {
            val issueReport = issueReportRepository.getIssueReportById(reportId)
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val technician = technicalServiceRepository.findTechnicalServiceById(technicianId)
                ?: return@useTransaction failure(IssueReportError.UserNotFound)
            val updatedIssueReport = issueReportRepository.assignIssueTo(issueReport, technician)
            return@useTransaction success(updatedIssueReport)
        }
    }

    fun deleteIssueReport(id: Int): IssueReportDeletionResult {
        return transactionInterface.useTransaction {
            issueReportRepository.getIssueReportById(id)
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val deletedIssue = issueReportRepository.deleteIssueReport(id)
            return@useTransaction success(deletedIssue)
        }
    }

    fun updateIssueReport(id: Int, description: String): IssueReportUpdateResult {
        return transactionInterface.useTransaction {
            val report = issueReportRepository.getIssueReportById(id)
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val updatedIssue = issueReportRepository.updateIssueReport(report, description)
            return@useTransaction success(updatedIssue)
        }
    }

}