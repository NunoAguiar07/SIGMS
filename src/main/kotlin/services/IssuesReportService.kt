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
        if (id.toInt() <= 0) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
        return transactionInterface.useTransaction {
           val issue = issueReportRepository.getIssueReportById(id.toInt())
               ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            return@useTransaction success(issue)
        }
    }

    fun createIssueReport(roomId: String?, description: String):IssueReportResult {
        if (roomId == null || roomId.toIntOrNull() == null) {
            return failure(IssueReportError.InvalidRoomId)
        }
        if (description.isBlank()) {
            return failure(IssueReportError.InvalidDescription)
        }
        if (roomId.toInt() <= 0) {
            return failure(IssueReportError.InvalidRoomId)
        }
       return transactionInterface.useTransaction {
           val room = roomRepository.getRoomById(roomId.toInt())
               ?: return@useTransaction failure(IssueReportError.InvalidRoomId)
            val newIssue = issueReportRepository.createIssueReport(room, description)
                ?: return@useTransaction failure(IssueReportError.FailedToAddToDatabase)
           return@useTransaction success(newIssue)
       }
    }

    fun deleteIssueReport(id: String): IssueReportDeletionResult {
        val parsedId = id.toInt()
        if (parsedId <= 0) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
        return transactionInterface.useTransaction {
            issueReportRepository.getIssueReportById(parsedId)
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val deletedIssue = issueReportRepository.deleteIssueReport(parsedId)
            return@useTransaction success(deletedIssue)
        }
    }

    fun updateIssueReport(id: String, roomId: String, description: String): IssueReportUpdateResult {
        val parsedId = id.toInt()
        val parsedRoomId = roomId.toInt()
        if (description.isBlank()) {
            return failure(IssueReportError.InvalidDescription)
        }
        if (parsedRoomId <= 0) {
            return failure(IssueReportError.InvalidRoomId)
        }
        if (parsedId <= 0) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
       return transactionInterface.useTransaction {
            issueReportRepository.getIssueReportById(parsedId)
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val room = issueReportRepository.getIssueReportById(parsedRoomId)
                ?: return@useTransaction failure(IssueReportError.InvalidRoomId)
            val updatedIssue = issueReportRepository.updateIssueReport(parsedId, room.id, description)
                ?: return@useTransaction failure(IssueReportError.FailedToUpdateInDatabase)
            return@useTransaction success(updatedIssue)
        }
    }
}