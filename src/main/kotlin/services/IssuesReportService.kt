package isel.leic.group25.services

import isel.leic.group25.db.entities.issues.IssueReport
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
    fun getAllIssueReports(limit:String?, offset:String?): IssueReportListResult {
        val newLimit = limit?.toInt() ?: 20
        if (newLimit <= 0 || newLimit > 100) {
            return failure(IssueReportError.InvalidIssueReportLimit)
        }
        val newOffset = offset?.toInt() ?: 0
        if (newOffset < 0) {
            return failure(IssueReportError.InvalidIssueReportOffSet)
        }
        return transactionInterface.useTransaction {
            val issues = issueReportRepository.getAllIssueReports(newLimit, newOffset)
           return@useTransaction success(issues)
        }
    }

    fun getIssueReportById(id: String?): IssueReportResult {
        if (id == null || id.toIntOrNull() == null) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
        if (id.toInt() < 0) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
        return transactionInterface.useTransaction {
           val issue = issueReportRepository.getIssueReportById(id.toInt())
               ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            return@useTransaction success(issue)
        }
    }

    fun getIssuesReportByRoomId(roomId: String?, limit:String?, offset:String?): IssueReportListResult {
        if (roomId == null || roomId.toIntOrNull() == null) {
            return failure(IssueReportError.InvalidRoomId)
        }
        if (roomId.toInt() <= 0) {
            return failure(IssueReportError.InvalidRoomId)
        }
        val newLimit = limit?.toInt() ?: 20
        if (newLimit <= 0 || newLimit > 100) {
            return failure(IssueReportError.InvalidIssueReportLimit)
        }
        val newOffset = offset?.toInt() ?: 0
        if (newOffset < 0) {
            return failure(IssueReportError.InvalidIssueReportOffSet)
        }
       return transactionInterface.useTransaction {
           val issues = issueReportRepository.getIssuesReportByRoomId(roomId.toInt(), newLimit, newOffset)
           return@useTransaction success(issues)
       }
    }

    fun createIssueReport(roomId: String?, description: String):IssueReportResult {
        if (roomId == null || roomId.toIntOrNull() == null) {
            return failure(IssueReportError.InvalidRoomId)
        }
        if (description.isBlank()) {
            return failure(IssueReportError.InvalidDescription)
        }
        if (roomId.toInt() < 0) {
            return failure(IssueReportError.InvalidRoomId)
        }
       return transactionInterface.useTransaction {
           val room = roomRepository.getRoomById(roomId.toInt())
               ?: return@useTransaction failure(IssueReportError.InvalidRoomId)
            val newIssue = issueReportRepository.createIssueReport(room, description)
           return@useTransaction success(newIssue)
       }
    }

    fun deleteIssueReport(id: String?): IssueReportDeletionResult {
        if (id == null || id.toIntOrNull() == null) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
        val parsedId = id.toInt()
        if (parsedId < 0) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
        return transactionInterface.useTransaction {
            issueReportRepository.getIssueReportById(parsedId)
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val deletedIssue = issueReportRepository.deleteIssueReport(parsedId)
            return@useTransaction success(deletedIssue)
        }
    }

    fun updateIssueReport(id: String?, description: String): IssueReportUpdateResult {
        if (id == null || id.toIntOrNull() == null) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
        val parsedId = id.toInt()
        if (description.isBlank()) {
            return failure(IssueReportError.InvalidDescription)
        }

        if (parsedId < 0) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
       return transactionInterface.useTransaction {
            val report = issueReportRepository.getIssueReportById(parsedId)
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val updatedIssue = issueReportRepository.updateIssueReport(report, description)
            return@useTransaction success(updatedIssue)
        }
    }
}