package services

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.repositories.issues.IssueReportRepository
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.services.IssuesReportService
import isel.leic.group25.services.errors.IssueReportError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import org.junit.jupiter.api.Assertions.assertEquals
import repositories.DatabaseTestSetup
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertTrue

class IssueReportServiceTest {
    private val issueReportRepository = IssueReportRepository(DatabaseTestSetup.database)
    private val roomRepository = RoomRepository(DatabaseTestSetup.database)
    private val transactionInterface = KTransaction(DatabaseTestSetup.database)

    private val issuesReportService = IssuesReportService(
        issueReportRepository = issueReportRepository,
        transactionInterface = transactionInterface,
        roomRepository = roomRepository
    )

    // Helper function to create test rooms
    private fun createTestRooms(count: Int = 1): List<Room> {
        return transactionInterface.useTransaction {
            (1..count).map { i ->
                roomRepository.createRoom(10, "Test Room $i")
            }
        }
    }

    // Helper function to create test issue reports
    private fun createTestIssueReports(count: Int = 1, room: Room): List<IssueReport> {
        return transactionInterface.useTransaction {
            (1..count).map { i ->
                issueReportRepository.createIssueReport(room, "Test Description $i")
            }
        }
    }

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `getAllIssueReports returns reports with default parameters`() {
        val room = createTestRooms()[0]
        val reports = createTestIssueReports(5, room)
        val result = issuesReportService.getAllIssueReports(null, null)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(5, successResult.size, "Expected 5 reports")
        assertTrue(reports.all { it in successResult }, "Expected all reports to be in the result")
    }

    @Test
    fun `getAllIssueReports fails with invalid limit`() {
        val result = issuesReportService.getAllIssueReports("200", null)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidIssueReportLimit,
            result.value,
            "Expected InvalidIssueReportLimit error"
        )
    }

    @Test
    fun `getAllIssueReports fails with invalid offset`() {
        val result = issuesReportService.getAllIssueReports(null, "-1")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidIssueReportOffSet,
            result.value,
            "Expected InvalidIssueReportOffSet error"
        )
    }

    @Test
    fun `getIssueReportById returns report when exists`() {
        val room = createTestRooms()[0]
        val report = createTestIssueReports(1, room)[0]
        val result = issuesReportService.getIssueReportById(report.id.toString())
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(report.id, successResult.id, "Expected report ID to match")
    }

    @Test
    fun `getIssueReportById fails with invalid ID`() {
        val result = issuesReportService.getIssueReportById("invalid")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidIssueReportId,
            result.value,
            "Expected InvalidIssueReportId error"
        )
    }

    @Test
    fun `getIssueReportById fails with non-existing ID`() {
        val result = issuesReportService.getIssueReportById("99999")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidIssueReportId,
            result.value,
            "Expected InvalidIssueReportId error"
        )
    }

    @Test
    fun `createIssueReport succeeds with valid parameters`() {
        val room = createTestRooms()[0]
        val result = issuesReportService.createIssueReport(room.id.toString(), "Test Description")
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(room.id, successResult.room.id, "Expected room ID to match")
        assertEquals("Test Description", successResult.description, "Expected description to match")
    }

    @Test
    fun `createIssueReport fails with blank description`() {
        val room = createTestRooms()[0]
        val result = issuesReportService.createIssueReport(room.id.toString(), "")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidDescription,
            result.value,
            "Expected InvalidDescription error"
        )
    }

    @Test
    fun `createIssueReport fails with invalid room ID`() {
        val result = issuesReportService.createIssueReport("invalid", "Test Description")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidRoomId,
            result.value,
            "Expected InvalidRoomId error"
        )
    }

    @Test
    fun `createIssueReport fails with non-existing room ID`() {
        val result = issuesReportService.createIssueReport("99999", "Test Description")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidRoomId,
            result.value,
            "Expected InvalidRoomId error"
        )
    }

    @Test
    fun `deleteIssueReport succeeds with valid ID`() {
        val room = createTestRooms()[0]
        val report = createTestIssueReports(1, room)[0]
        val result = issuesReportService.deleteIssueReport(report.id.toString())
        assertTrue(result is Success, "Expected Success")
        assertTrue(result.value, "Expected deletion to be successful")
    }

    @Test
    fun `deleteIssueReport fails with invalid ID`() {
        val result = issuesReportService.deleteIssueReport("invalid")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidIssueReportId,
            result.value,
            "Expected InvalidIssueReportId error"
        )
    }

    @Test
    fun `deleteIssueReport fails with non-existing ID`() {
        val result = issuesReportService.deleteIssueReport("99999")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidIssueReportId,
            result.value,
            "Expected InvalidIssueReportId error"
        )
    }

    @Test
    fun `updateIssueReport succeeds with valid parameters`() {
        val room = createTestRooms()[0]
        val report = createTestIssueReports(1, room)[0]
        val result = issuesReportService.updateIssueReport(
            report.id.toString(),
            "Updated Description"
        )
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(report.id, successResult.id, "Expected report ID to match")
        assertEquals("Updated Description", successResult.description, "Expected description to match")
    }

    @Test
    fun `updateIssueReport fails with blank description`() {
        val room = createTestRooms()[0]
        val report = createTestIssueReports(1, room)[0]
        val result = issuesReportService.updateIssueReport(
            report.id.toString(),
            ""
        )
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidDescription,
            result.value,
            "Expected InvalidDescription error"
        )
    }

    @Test
    fun `updateIssueReport fails with invalid report ID`() {
        val result = issuesReportService.updateIssueReport("invalid",  "Updated Description")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidIssueReportId,
            result.value,
            "Expected InvalidIssueReportId error"
        )
    }

    @Test
    fun `updateIssueReport fails with non-existing report ID`() {
        val result = issuesReportService.updateIssueReport("99999", "Updated Description")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidIssueReportId,
            result.value,
            "Expected InvalidIssueReportId error"
        )
    }
}