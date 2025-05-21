package tables

import isel.leic.group25.db.tables.Tables.Companion.issueReports
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.ktorm.dsl.eq
import org.ktorm.entity.firstOrNull
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

class IssueTableTest {
    private val dbHelper = TestDatabaseHelper()
    private val database get() = dbHelper.database

    @BeforeTest
    fun setup() {
        dbHelper.setup()
    }

    @AfterTest
    fun cleanup() {
        dbHelper.cleanup()
    }

    @Test
    fun `Should create a new issue`(){
        val newRoom = dbHelper.createRoom(
            name = "G.0.02",
            capacity = 15
        )
        val newUser = dbHelper.createUser(
            emailSuffix = "69",
            usernameSuffix = "123",
            password = "supersecretpassword123"
        )
        val newIssue = dbHelper.createIssueReport(
            room = newRoom,
            user = newUser
        )
        val issue = database.issueReports.firstOrNull { it.id eq newIssue.id }
        assertNotNull(issue, "Issue should exist in database")
        assertEquals(newIssue.id, issue.id)
        assertEquals(newIssue.room.id, issue.room.id)
        assertEquals(newIssue.description, newIssue.description)
    }

    @Test
    fun `Should update an issue`(){
        val newRoom = dbHelper.createRoom(
            name = "G.0.02",
            capacity = 15
        )
        val newUser = dbHelper.createUser(
            emailSuffix = "69",
            usernameSuffix = "123",
            password = "supersecretpassword123"
        )
        val newIssue = dbHelper.createIssueReport(
            room = newRoom,
            user = newUser
        )
        val issue = database.issueReports.firstOrNull { it.id eq newIssue.id }
        assertNotNull(issue, "Issue should exist in database")
        assertEquals(newIssue.id, issue.id)
        assertEquals(newIssue.room.id, issue.room.id)
        assertEquals(newIssue.description, issue.description)
        newIssue.description += "Veni Vini Vici."
        newIssue.flushChanges()
        val changedIssue = database.issueReports.firstOrNull { it.id eq newIssue.id }
        assertNotNull(changedIssue, "Issue should exist in database")
        assertEquals(newIssue.id, changedIssue.id)
        assertEquals(newIssue.room.id, changedIssue.room.id)
        assertEquals(newIssue.description, changedIssue.description)
    }

    @Test
    fun `Should delete an issue`(){
        val newRoom = dbHelper.createRoom(
            name = "G.0.02",
            capacity = 15
        )
        val newUser = dbHelper.createUser(
            emailSuffix = "69",
            usernameSuffix = "123",
            password = "supersecretpassword123"
        )
        val newIssue = dbHelper.createIssueReport(
            room = newRoom,
            user = newUser
        )
        newIssue.delete()
        assertNull(database.issueReports.firstOrNull { it.id eq newIssue.id })
    }
}