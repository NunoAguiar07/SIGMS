package repositories.issues

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.issues.IssueReportRepository
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.users.UserRepository
import repositories.DatabaseTestSetup
import repositories.DatabaseTestSetup.Companion.database
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class IssueReportRepositoryTest {
    private val kTormCommand = KtormCommand(database)
    private val issueReportRepository = IssueReportRepository(database)
    private val userRepository = UserRepository(database)
    private val universityRepository = UniversityRepository(database)
    private val roomRepository = RoomRepository(database)


    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `Should create a new issue report and find it by id`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university, it.authProvider) }
            val room = roomRepository.createRoom(20, "testRoom", newUniversity)
            val newIssueReport = issueReportRepository.createIssueReport(newUser ,room, "testDescription")
            val foundIssueReport = issueReportRepository.getIssueReportById(newIssueReport.id)
            assertNotNull(foundIssueReport, "Expected to find the issue report by ID")
            assert(foundIssueReport.description == newIssueReport.description)
        }
    }

    @Test
    fun `Should create a new issue report and find it by description`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university, it.authProvider) }
            val room = roomRepository.createRoom(20, "testRoom", newUniversity)
            val newIssueReport = issueReportRepository.createIssueReport(newUser, room, "testDescription")
            val foundIssueReport = issueReportRepository.getAllIssueReports(10, 0).firstOrNull { it.description == newIssueReport.description }
            assertNotNull(foundIssueReport)
            assert(foundIssueReport.description == newIssueReport.description)
        }
    }

    @Test
    fun `Should get all issue reports`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university, it.authProvider) }
            val room1 = roomRepository.createRoom(20, "testRoom1", newUniversity)
            val room2 = roomRepository.createRoom(30, "testRoom2", newUniversity)
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
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university, it.authProvider) }
            val room = roomRepository.createRoom(20, "testRoom", newUniversity)
            val newIssueReport = issueReportRepository.createIssueReport(newUser, room, "testDescription")
            val deleteResult = issueReportRepository.deleteIssueReport(newIssueReport.id)
            assert(deleteResult)
            val foundIssueReport = issueReportRepository.getIssueReportById(newIssueReport.id)
            assert(foundIssueReport == null)
        }
    }
    @Test
    fun `Should update an issue report`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("testUniversity")
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university, it.authProvider) }
            val room = roomRepository.createRoom(20, "testRoom", newUniversity)
            val newIssueReport = issueReportRepository.createIssueReport(newUser, room, "testDescription")
            val updatedIssueReport = issueReportRepository.updateIssueReport(newIssueReport,"updatedDescription")
            assert(updatedIssueReport.description == "updatedDescription")
        }
    }
    @Test
    fun `Should assign a technician to issue and retrieve it by userId`() {
        kTormCommand.useTransaction {
            val university = universityRepository.createUniversity("uni")
            val user = userRepository.createWithRole("student@test.com", "student", User.hashPassword("pass"), Role.STUDENT, university, "local")
            val technician = userRepository.createWithRole("tech@test.com", "tech", User.hashPassword("pass"), Role.TECHNICAL_SERVICE, university, "local")
            val room = roomRepository.createRoom(101, "room101", university)
            val issue = issueReportRepository.createIssueReport(user, room, "printer not working")

            val assigned = issueReportRepository.assignIssueTo(issue, technician.toTechnicalService(database))
            val foundByTech = issueReportRepository.getIssueReportsByUserId(technician.id, 10, 0)

            assertEquals(technician.id, assigned.assignedTo?.user?.id)
            assertEquals(1, foundByTech.size)
            assertEquals(assigned.id, foundByTech[0].id)
        }
    }
    @Test
    fun `Should unassign technician from issue and include it in unassigned list`() {
        kTormCommand.useTransaction {
            val university = universityRepository.createUniversity("uni")
            val user = userRepository.createWithRole("user@test.com", "user", User.hashPassword("pass"), Role.STUDENT, university, "local")
            val tech = userRepository.createWithRole("tech@test.com", "tech", User.hashPassword("pass"), Role.TECHNICAL_SERVICE, university, "local")
            val room = roomRepository.createRoom(102, "room102", university)
            val issue = issueReportRepository.createIssueReport(user, room, "light broken")

            issueReportRepository.assignIssueTo(issue, tech.toTechnicalService(database))
            val unassigned = issueReportRepository.unassignTechnicianFromIssueReport(issue)
            val unassignedList = issueReportRepository.getAllUnassignedIssueReports(10, 0)

            assertEquals(null, unassigned.assignedTo)
            assert(unassignedList.any { it.id == issue.id })
        }
    }
    @Test
    fun `Should get issue reports by roomId`() {
        kTormCommand.useTransaction {
            val university = universityRepository.createUniversity("uni")
            val user = userRepository.createWithRole("user@test.com", "user", User.hashPassword("pass"), Role.STUDENT, university, "local")
            val roomA = roomRepository.createRoom(201, "roomA", university)
            val roomB = roomRepository.createRoom(202, "roomB", university)

            issueReportRepository.createIssueReport(user, roomA, "Issue A1")
            issueReportRepository.createIssueReport(user, roomB, "Issue B1")
            issueReportRepository.createIssueReport(user, roomA, "Issue A2")

            val roomAIssues = issueReportRepository.getIssuesReportByRoomId(roomA.id, 10, 0)
            assertEquals(2, roomAIssues.size)
            assert(roomAIssues.all { it.room.id == roomA.id })
        }
    }




}