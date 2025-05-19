package services

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.services.IssuesReportService
import isel.leic.group25.services.errors.IssueReportError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.issues.MockIssueReportRepository
import mocks.repositories.rooms.MockRoomRepository
import mocks.repositories.timetables.MockUniversityRepository
import mocks.repositories.users.MockTechnicalServiceRepository
import mocks.repositories.users.MockUserRepository
import mocks.repositories.utils.MockTransaction
import org.junit.jupiter.api.Assertions.assertEquals
import repositories.DatabaseTestSetup
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertTrue

class IssueReportServiceTest {
    private val issueReportRepository = MockIssueReportRepository()
    private val roomRepository = MockRoomRepository()
    private val userRepository = MockUserRepository()
    private val technicalServiceRepository = MockTechnicalServiceRepository()
    private val universityRepository = MockUniversityRepository()
    private val transactionInterface = MockTransaction()

    private val issuesReportService = IssuesReportService(
        issueReportRepository = issueReportRepository,
        userRepository = userRepository,
        technicalServiceRepository = technicalServiceRepository,
        transactionInterface = transactionInterface,
        roomRepository = roomRepository
    )

    // Helper function to create test rooms
    private fun createTestRooms(count: Int = 1): List<Room> {
        return transactionInterface.useTransaction {
            (1..count).map { i ->
                val university = universityRepository.createUniversity("Test University $i")
                roomRepository.createRoom(10, "Test Room $i", university)
            }
        }
    }

    // Helper function to create test issue reports
    private fun createTestIssueReports(count: Int = 1, room: Room, user: User): List<IssueReport> {
        return transactionInterface.useTransaction {
            (1..count).map { i ->
                issueReportRepository.createIssueReport(user, room, "Test Description $i")
            }
        }
    }

    private fun createTestUser(role: Role = Role.STUDENT): User {
        return transactionInterface.useTransaction {
            val university = universityRepository.createUniversity("Test University")
            val user = User {
                email = "test@test.com"
                username = "testuser"
                password = User.hashPassword("test123!")
                authProvider = "local"
                profileImage = byteArrayOf(1, 2, 3)
                this.university = university
            }.let {
                userRepository.createWithRole(it.email, it.username, it.password, role, it.university, it.authProvider)
            }
            return@useTransaction user
        }
    }

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `getAllIssueReports returns reports with default parameters`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val reports = createTestIssueReports(5, room, user)
        val result = issuesReportService.getAllIssueReports(10, 0)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(5, successResult.size, "Expected 5 reports")
        assertTrue(reports.all { it in successResult }, "Expected all reports to be in the result")
    }

    @Test
    fun `getIssueReportById returns report when exists`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val report = createTestIssueReports(1, room, user)[0]
        val result = issuesReportService.getIssueReportById(report.id)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(report.id, successResult.id, "Expected report ID to match")
    }

    @Test
    fun `getIssueReportById fails with non-existing ID`() {
        val result = issuesReportService.getIssueReportById(999)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.IssueReportNotFound,
            result.value,
            "Expected InvalidIssueReportId error"
        )
    }

    @Test
    fun `createIssueReport succeeds with valid parameters`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val result = issuesReportService.createIssueReport(user.id, room.id, "Test Description")
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(room.id, successResult.room.id, "Expected room ID to match")
        assertEquals("Test Description", successResult.description, "Expected description to match")
    }

    @Test
    fun `createIssueReport fails with non-existing room ID`() {
        val user = createTestUser()
        val result = issuesReportService.createIssueReport(user.id, 999, "Test Description")
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
        val user = createTestUser()
        val report = createTestIssueReports(1, room, user)[0]
        val result = issuesReportService.deleteIssueReport(report.id)
        assertTrue(result is Success, "Expected Success")
        assertTrue(result.value, "Expected deletion to be successful")
    }

    @Test
    fun `deleteIssueReport fails with non-existing ID`() {
        val result = issuesReportService.deleteIssueReport(999)
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
        val user = createTestUser()
        val report = createTestIssueReports(1, room, user)[0]
        val result = issuesReportService.updateIssueReport(
            report.id,
            "Updated Description"
        )
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(report.id, successResult.id, "Expected report ID to match")
        assertEquals("Updated Description", successResult.description, "Expected description to match")
    }

    @Test
    fun `updateIssueReport fails with non-existing report ID`() {
        val result = issuesReportService.updateIssueReport(999, "Updated Description")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            IssueReportError.InvalidIssueReportId,
            result.value,
            "Expected InvalidIssueReportId error"
        )
    }
}