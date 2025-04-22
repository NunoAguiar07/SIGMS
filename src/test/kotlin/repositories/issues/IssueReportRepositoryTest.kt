package repositories.issues

import isel.leic.group25.db.repositories.issues.IssueReportRepository
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.rooms.RoomRepository
import repositories.DatabaseTestSetup
import repositories.DatabaseTestSetup.Companion.database
import kotlin.test.AfterTest
import kotlin.test.Test

class IssueReportRepositoryTest {
    private val kTransaction = KTransaction(database)
    private val issueReportRepository = IssueReportRepository(database)
    private val roomRepository = RoomRepository(database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `Should create a new issue report and find it by id`() {
        kTransaction.useTransaction {
            val room = roomRepository.createRoom(20, "testRoom")
            val newIssueReport = issueReportRepository.createIssueReport(room, "testDescription")
            val foundIssueReport = issueReportRepository.getIssueReportById(newIssueReport.id)
            assert(foundIssueReport != null)
            assert(foundIssueReport?.description == newIssueReport.description)
        }
    }

    @Test
    fun `Should create a new issue report and find it by description`() {
        kTransaction.useTransaction {
            val room = roomRepository.createRoom(20, "testRoom")
            val newIssueReport = issueReportRepository.createIssueReport(room, "testDescription")
            val foundIssueReport = issueReportRepository.getAllIssueReports(10, 0).firstOrNull { it.description == newIssueReport.description }
            assert(foundIssueReport != null)
            assert(foundIssueReport?.description == newIssueReport.description)
        }
    }

    @Test
    fun `Should get all issue reports`() {
        kTransaction.useTransaction {
            val room1 = roomRepository.createRoom(20, "testRoom1")
            val room2 = roomRepository.createRoom(30, "testRoom2")
            val newIssueReport1 = issueReportRepository.createIssueReport(room1, "testDescription1")
            val newIssueReport2 = issueReportRepository.createIssueReport(room2, "testDescription2")
            val issueReports = issueReportRepository.getAllIssueReports(10, 0)
            assert(issueReports.size == 2)
            assert(issueReports.contains(newIssueReport1))
            assert(issueReports.contains(newIssueReport2))
        }
    }
    @Test
    fun `Should delete an issue report`() {
        kTransaction.useTransaction {
            val room = roomRepository.createRoom(20, "testRoom")
            val newIssueReport = issueReportRepository.createIssueReport(room, "testDescription")
            val deleteResult = issueReportRepository.deleteIssueReport(newIssueReport.id)
            assert(deleteResult)
            val foundIssueReport = issueReportRepository.getIssueReportById(newIssueReport.id)
            assert(foundIssueReport == null)
        }
    }
    @Test
    fun `Should update an issue report`() {
        kTransaction.useTransaction {
            val room = roomRepository.createRoom(20, "testRoom")
            val newIssueReport = issueReportRepository.createIssueReport(room, "testDescription")
            val updatedIssueReport = issueReportRepository.updateIssueReport(newIssueReport.id, room.id, "updatedDescription")
            assert(updatedIssueReport != null)
            assert(updatedIssueReport?.description == "updatedDescription")
        }
    }
    @Test
    fun `Should not update an issue report with invalid room id`() {
        kTransaction.useTransaction {
            val room = roomRepository.createRoom(20, "testRoom")
            val newIssueReport = issueReportRepository.createIssueReport(room, "testDescription")
            val updatedIssueReport = issueReportRepository.updateIssueReport(newIssueReport.id, -1, "updatedDescription")
            assert(updatedIssueReport == null)
        }
    }

}