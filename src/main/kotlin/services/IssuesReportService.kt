package isel.leic.group25.services

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.types.Role
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

    fun createIssueReport(userId: Int?, roomId: String?, description: String):IssueReportResult {
        if (userId == null) {
            return failure(IssueReportError.InvalidUserId)
        }
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
            val user = userRepository.findById(userId) ?: return@useTransaction failure(IssueReportError.UserNotFound)
            val newIssue = issueReportRepository.createIssueReport(user, room, description)
            return@useTransaction success(newIssue)
        }
    }

    fun assignTechnicianToIssueReport(technicianId: Int?, reportId: String?) : IssueReportResult{
        if (technicianId == null) {
            return failure(IssueReportError.InvalidUserId)
        }
        if (reportId == null || reportId.toIntOrNull() == null) {
            return failure(IssueReportError.InvalidUserId)
        }
        return transactionInterface.useTransaction {
            val issueReport = issueReportRepository.getIssueReportById(reportId.toInt())
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val technician = technicalServiceRepository.findTechnicalServiceById(technicianId)
                ?: return@useTransaction failure(IssueReportError.UserNotFound)
            val updatedIssueReport = issueReportRepository.assignIssueTo(issueReport, technician)
            return@useTransaction success(updatedIssueReport)
        }
    }

    fun deleteIssueReport(id: String?, role :String?): IssueReportDeletionResult {
        if (id == null || id.toIntOrNull() == null) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
        val parsedId = id.toInt()
        if (parsedId < 0) {
            return failure(IssueReportError.InvalidIssueReportId)
        }
        if(isTechnicalServices(role)) failure(IssueReportError.InvalidRole)
        return transactionInterface.useTransaction {
            issueReportRepository.getIssueReportById(parsedId)
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val deletedIssue = issueReportRepository.deleteIssueReport(parsedId)
            return@useTransaction success(deletedIssue)
        }
    }

    fun updateIssueReport(id: String?, description: String, role:String?): IssueReportUpdateResult {
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
        if(isTechnicalServices(role)) failure(IssueReportError.InvalidRole)
       return transactionInterface.useTransaction {
            val report = issueReportRepository.getIssueReportById(parsedId)
                ?: return@useTransaction failure(IssueReportError.InvalidIssueReportId)
            val updatedIssue = issueReportRepository.updateIssueReport(report, description)
            return@useTransaction success(updatedIssue)
        }
    }

    private fun isTechnicalServices(role :String?): Boolean {
        return (role != null && role != Role.TECHNICAL_SERVICE.name)
    }

}