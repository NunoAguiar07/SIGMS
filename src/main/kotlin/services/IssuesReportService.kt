package isel.leic.group25.services

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.issues.interfaces.IssueReportRepositoryInterface
import isel.leic.group25.db.repositories.rooms.interfaces.RoomRepositoryInterface
import isel.leic.group25.services.errors.IssueReportError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success

typealias IssueReportListResult = Either<IssueReportError, List<IssueReport>>

typealias IssueReportResult = Either<IssueReportError, IssueReport>
typealias IssueReportUpdateResult = Either<IssueReportError, IssueReport>

typealias IssueReportDeletionResult = Either<IssueReportError, Boolean>

class IssuesReportService(private val issueReportRepository: IssueReportRepositoryInterface,
                          private val transactionInterface: TransactionInterface,
                          private val roomRepository: RoomRepositoryInterface
) {
    fun getAllIssueReports(): IssueReportListResult {
       return transactionInterface.useTransaction {
            val issues = issueReportRepository.getAllIssueReports()
           return@useTransaction success(issues)
        }
    }

    fun getIssueReportById(id: Int): IssueReportResult {
        if (id <= 0) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
        return transactionInterface.useTransaction {
           val issue = issueReportRepository.getIssueReportById(id)
               ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            return@useTransaction success(issue)
        }
    }

    fun createIssueReport(roomId: Int, description: String):IssueReportResult {
        if (description.isBlank()) {
            return failure(IssueReportError.InvalidDescription)
        }
        if (roomId <= 0) {
            return failure(IssueReportError.InvalidRoomId)
        }
       return transactionInterface.useTransaction {
           val room = roomRepository.getRoomById(roomId)
               ?: return@useTransaction failure(IssueReportError.InvalidRoomId)
            val newIssue = issueReportRepository.createIssueReport(room, description)
                ?: return@useTransaction failure(IssueReportError.FailedToAddToDatabase)
           return@useTransaction success(newIssue)
       }
    }

    fun deleteIssueReport(id: Int): IssueReportDeletionResult {
        if (id <= 0) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
        return transactionInterface.useTransaction {
            issueReportRepository.getIssueReportById(id)
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val deletedIssue = issueReportRepository.deleteIssueReport(id)
            return@useTransaction success(deletedIssue)
        }
    }

    fun updateIssueReport(id: Int, roomId: Int, description: String): IssueReportUpdateResult {
        if (description.isBlank()) {
            return failure(IssueReportError.InvalidDescription)
        }
        if (roomId <= 0) {
            return failure(IssueReportError.InvalidRoomId)
        }
        if (id <= 0) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
       return transactionInterface.useTransaction {
            val issueReport = issueReportRepository.getIssueReportById(id)
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val room = issueReportRepository.getIssueReportById(roomId)
                ?: return@useTransaction failure(IssueReportError.InvalidRoomId)
            val updatedIssue = issueReportRepository.updateIssueReport(id, room.id, description)
                ?: return@useTransaction failure(IssueReportError.FailedToUpdateInDatabase)
            return@useTransaction success(updatedIssue)
        }
    }
}