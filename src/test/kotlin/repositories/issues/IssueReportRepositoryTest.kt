package repositories.issues

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.issues.IssueReportRepository
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.db.repositories.users.UserRepository
import repositories.DatabaseTestSetup
import repositories.DatabaseTestSetup.Companion.database
import kotlin.test.*

class IssueReportRepositoryTest {
    private val kTransaction = KTransaction(database)
    private val issueReportRepository = IssueReportRepository(database)
    private val userRepository = UserRepository(database)
    private val roomRepository = RoomRepository(database)


    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `Should create a new issue report and find it by id`() {
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT) }
            val room = roomRepository.createRoom(20, "testRoom")
            val newIssueReport = issueReportRepository.createIssueReport(newUser ,room, "testDescription")
            val foundIssueReport = issueReportRepository.getIssueReportById(newIssueReport.id)
            assertNotNull(foundIssueReport, "Expected to find the issue report by ID")
            assert(foundIssueReport.description == newIssueReport.description)
        }
    }

    @Test
    fun `Should create a new issue report and find it by description`() {
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT) }
            val room = roomRepository.createRoom(20, "testRoom")
            val newIssueReport = issueReportRepository.createIssueReport(newUser, room, "testDescription")
            val foundIssueReport = issueReportRepository.getAllIssueReports(10, 0).firstOrNull { it.description == newIssueReport.description }
            assertNotNull(foundIssueReport)
            assert(foundIssueReport.description == newIssueReport.description)
        }
    }

    @Test
    fun `Should get all issue reports`() {
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT) }
            val room1 = roomRepository.createRoom(20, "testRoom1")
            val room2 = roomRepository.createRoom(30, "testRoom2")
            val newIssueReport1 = issueReportRepository.createIssueReport(newUser, room1, "testDescription1")
            val newIssueReport2 = issueReportRepository.createIssueReport(newUser, room2, "testDescription2")
            val issueReports = issueReportRepository.getAllIssueReports(10, 0)
            assert(issueReports.size == 2)
            assertEquals(newIssueReport1.id, issueReports[0].id)
            assertEquals(newIssueReport2.id, issueReports[1].id)
            assertEquals(newIssueReport1.description, issueReports[0].description)
            assertEquals(newIssueReport2.description, issueReports[1].description)
            assertEquals(newIssueReport1.room, issueReports[0].room)
            assertEquals(newIssueReport2.room, issueReports[1].room)
            assertEquals(newIssueReport1.createdBy.id, issueReports[0].createdBy.id)
            assertEquals(newIssueReport2.createdBy.id, issueReports[1].createdBy.id)

        }
    }
    @Test
    fun `Should delete an issue report`() {
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT) }
            val room = roomRepository.createRoom(20, "testRoom")
            val newIssueReport = issueReportRepository.createIssueReport(newUser, room, "testDescription")
            val deleteResult = issueReportRepository.deleteIssueReport(newIssueReport.id)
            assert(deleteResult)
            val foundIssueReport = issueReportRepository.getIssueReportById(newIssueReport.id)
            assert(foundIssueReport == null)
        }
    }
    @Test
    fun `Should update an issue report`() {
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT) }
            val room = roomRepository.createRoom(20, "testRoom")
            val newIssueReport = issueReportRepository.createIssueReport(newUser, room, "testDescription")
            val updatedIssueReport = issueReportRepository.updateIssueReport(newIssueReport,"updatedDescription")
            assert(updatedIssueReport.description == "updatedDescription")
        }
    }

}