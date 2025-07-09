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
import org.ktorm.database.Database
import kotlin.test.Test
import kotlin.test.assertEquals
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

    private fun createTestRooms(count: Int = 1): List<Room> {
        return mockRepositories.ktormCommand.useTransaction {
            (1..count).map { i ->
                val university = mockRepositories.from({universityRepository}) {
                    createUniversity("Test University $i")
                }
                mockRepositories.from({roomRepository}) {
                    createRoom(10, "Test Room $i", university)
                }
            }
        }
    }

    private fun createTestUser(role: Role = Role.STUDENT): User {
        return mockRepositories.ktormCommand.useTransaction {
            val university = mockRepositories.from({universityRepository}) {
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
                mockRepositories.from({userRepository}) {
                    createWithRole(it.email, it.username, it.password, role, it.university, it.authProvider)
                }
            }
            user
        }
    }


    private fun createTestIssueReports(count: Int = 1, room: Room, user: User): List<IssueReport> {
        return mockRepositories.ktormCommand.useTransaction {
            (1..count).map { i ->
                mockRepositories.from({issueReportRepository}) {
                    createIssueReport(user, room, "Test Description $i")
                }
            }
        }
    }

    @Test
    fun `getAllIssueReports returns reports with default parameters`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val reports = createTestIssueReports(5, room, user)
        val result = issuesReportService.getAllIssueReports(10, 0, unassigned = false)
        assertTrue(result is Success)
        assertEquals(5, result.value.size)
        assertTrue(reports.all { it in result.value })
    }

    @Test
    fun `getAllIssueReports returns only unassigned reports when unassigned=true`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val reports = createTestIssueReports(3, room, user)
        val result = issuesReportService.getAllIssueReports(10, 0, unassigned = true)
        assertTrue(result is Success)
        assertEquals(3, result.value.size)
    }
    @Test
    fun `getIssuesReportByRoomId returns correct reports`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val reports = createTestIssueReports(4, room, user)
        val result = issuesReportService.getIssuesReportByRoomId(room.id, 10, 0)
        assertTrue(result is Success)
        assertEquals(4, result.value.size)
    }

    @Test
    fun `getIssueReportById returns report when exists`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val report = createTestIssueReports(1, room, user)[0]
        val result = issuesReportService.getIssueReportById(report.id)
        assertTrue(result is Success)
        assertEquals(report.id, result.value.id)
    }

    @Test
    fun `getIssueReportById fails with non-existing ID`() {
        val result = issuesReportService.getIssueReportById(999)
        assertTrue(result is Failure)
        assertEquals(IssueReportError.IssueReportNotFound, result.value)
    }

    @Test
    fun `createIssueReport succeeds with valid parameters`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val result = issuesReportService.createIssueReport(user.id, room.id, "Test Description")
        assertTrue(result is Success)
        assertEquals(room.id, result.value.room.id)
    }

    @Test
    fun `createIssueReport fails with non-existing room ID`() {
        val user = createTestUser()
        val result = issuesReportService.createIssueReport(user.id, 999, "Test Description")
        assertTrue(result is Failure)
        assertEquals(IssueReportError.InvalidRoomId, result.value)
    }

    @Test
    fun `deleteIssueReport succeeds with valid ID`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val report = createTestIssueReports(1, room, user)[0]
        val result = issuesReportService.deleteIssueReport(report.id)
        assertTrue(result is Success)
        assertTrue(result.value)
    }

    @Test
    fun `deleteIssueReport fails with non-existing ID`() {
        val result = issuesReportService.deleteIssueReport(999)
        assertTrue(result is Failure)
        assertEquals(IssueReportError.InvalidIssueReportId, result.value)
    }

    @Test
    fun `updateIssueReport succeeds with valid parameters`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val report = createTestIssueReports(1, room, user)[0]
        val result = issuesReportService.updateIssueReport(report.id, "Updated Description")
        assertTrue(result is Success)
        assertEquals("Updated Description", result.value.description)
    }

    @Test
    fun `updateIssueReport fails with non-existing report ID`() {
        val result = issuesReportService.updateIssueReport(999, "Updated Description")
        assertTrue(result is Failure)
        assertEquals(IssueReportError.InvalidIssueReportId, result.value)
    }



    @Test
    fun `assignTechnicianToIssueReport fails with invalid technician ID`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val report = createTestIssueReports(1, room, user)[0]
        val result = issuesReportService.assignTechnicianToIssueReport(999, report.id)
        assertTrue(result is Failure)
        assertEquals(IssueReportError.UserNotFound, result.value)
    }

    @Test
    fun `unassignTechnicianFromIssueReport fails when technician not assigned`() {
        val room = createTestRooms()[0]
        val user = createTestUser()
        val technician = createTestUser(Role.TECHNICAL_SERVICE)
        val otherTech = createTestUser(Role.TECHNICAL_SERVICE)
        val report = createTestIssueReports(1, room, user)[0]
        issuesReportService.assignTechnicianToIssueReport(technician.id, report.id)
        val result = issuesReportService.unassignTechnicianFromIssueReport(otherTech.id, report.id)
        assertTrue(result is Failure)
        assertEquals(IssueReportError.NotAssignedToIssueReport, result.value)
    }
}
