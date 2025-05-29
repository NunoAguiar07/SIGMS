package services

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.services.IssuesReportService
import isel.leic.group25.services.errors.IssueReportError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.MockRepositories
import org.junit.jupiter.api.Assertions.assertEquals
import org.ktorm.database.Database
import kotlin.test.Test
import kotlin.test.assertTrue

class IssueReportServiceTest {
    val mockDB = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        password = ""
    )
    private val mockRepositories = MockRepositories(mockDB)

    private val issuesReportService = IssuesReportService(
        mockRepositories,
        mockRepositories.ktormCommand
    )

    // Helper function to create test rooms
    private fun createTestRooms(count: Int = 1): List<Room> {
        return mockRepositories.ktormCommand.useTransaction {
            (1..count).map { i ->
                val university = mockRepositories.from({universityRepository}){
                    createUniversity("Test University $i")
                }
                mockRepositories.from({roomRepository}){
                    createRoom(10, "Test Room $i", university)
                }
            }
        }
    }

    // Helper function to create test issue reports
    private fun createTestIssueReports(count: Int = 1, room: Room, user: User): List<IssueReport> {
        return mockRepositories.ktormCommand.useTransaction {
            (1..count).map { i ->
                mockRepositories.from({issueReportRepository}){
                    createIssueReport(user, room, "Test Description $i")
                }
            }
        }
    }

    private fun createTestUser(role: Role = Role.STUDENT): User {
        return mockRepositories.ktormCommand.useTransaction {
            val university = mockRepositories.from({universityRepository}){
                createUniversity("Test University")
            }
            val user = User {
                email = "test@test.com"
                username = "testuser"
                password = User.hashPassword("test123!")
                authProvider = "local"
                profileImage = byteArrayOf(1, 2, 3)
                this.university = university
            }.let {
                mockRepositories.from({userRepository}){
                    createWithRole(it.email, it.username, it.password, role, it.university, it.authProvider)
                }
            }
            return@useTransaction user
        }
    }

    @Test
    fun `getAllIssueReports returns reports with default parameters`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val reports = createTestIssueReports(5, room, user)
        val unassigned = false
        val result = issuesReportService.getAllIssueReports(10, 0, unassigned)
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