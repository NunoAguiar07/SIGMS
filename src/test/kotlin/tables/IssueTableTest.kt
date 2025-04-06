package tables

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.tables.Tables.Companion.issueReports
import isel.leic.group25.db.tables.Tables.Companion.rooms
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.first
import org.ktorm.entity.firstOrNull
import java.io.StringReader
import java.sql.Connection
import javax.sql.DataSource
import kotlin.test.AfterTest
import kotlin.test.Test

class IssueTableTest {
    private val connection: Connection
    private val database: Database

    @AfterTest
    fun clearDB(){
        RunScript.execute(connection, StringReader("""
            DELETE FROM ISSUE_REPORT;
            DELETE FROM ROOM;
            """)
        )
    }

    init {
        val dataSource: DataSource = JdbcDataSource().apply {
            setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
            user = "sa"
            password = ""
        }
        connection = dataSource.connection
        database = Database.connect(dataSource)
        RunScript.execute(connection, StringReader("""
            CREATE TABLE IF NOT EXISTS ROOM (
                id SERIAL PRIMARY KEY,
                capacity INT NOT NULL,
                CHECK(capacity > 0)
            );
            
            CREATE TABLE IF NOT EXISTS ISSUE_REPORT (
                id SERIAL PRIMARY KEY,
                room_id INT NOT NULL REFERENCES ROOM(id) ON DELETE CASCADE,
                description TEXT NOT NULL
            );
        """)
        )
    }

    @Test
    fun `Should create a new issue`(){
        val newRoom = Room {
            capacity = 15
        }.also{ database.rooms.add(it) }
        val newIssue = IssueReport {
            room = newRoom
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean efficitur ex ac dui dictum " +
                    "blandit. Morbi gravida eget nunc at viverra. Donec maximus nisl sit amet nibh dignissim, " +
                    "non fermentum tellus luctus. In laoreet libero lacinia tellus finibus placerat. Integer vitae " +
                    "sagittis sapien. Aenean et iaculis sapien, a finibus quam. Sed id lacus id mauris dictum " +
                    "vestibulum quis non lacus. Nunc aliquam fringilla turpis vitae semper. Praesent nisl diam, " +
                    "facilisis at iaculis eget, viverra ac sem. Aenean sagittis diam id aliquet condimentum. " +
                    "Integer accumsan fringilla scelerisque. Duis consectetur pharetra nibh nec faucibus. " +
                    "Praesent consequat aliquam finibus. Fusce quis ante nec mi cursus facilisis. "
        }.also{ database.issueReports.add(it) }
        val issue = database.issueReports.first { it.id eq newIssue.id }
        assertEquals(newIssue.id, issue.id)
        assertEquals(newIssue.room.id, issue.room.id)
        assertEquals(newIssue.description, newIssue.description)
    }

    @Test
    fun `Should update an issue`(){
        val newRoom = Room {
            capacity = 15
        }.also{ database.rooms.add(it) }
        val newIssue = IssueReport {
            room = newRoom
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean efficitur ex ac dui dictum " +
                    "blandit. Morbi gravida eget nunc at viverra. Donec maximus nisl sit amet nibh dignissim, " +
                    "non fermentum tellus luctus. In laoreet libero lacinia tellus finibus placerat. Integer vitae " +
                    "sagittis sapien. Aenean et iaculis sapien, a finibus quam. Sed id lacus id mauris dictum " +
                    "vestibulum quis non lacus. Nunc aliquam fringilla turpis vitae semper. Praesent nisl diam, " +
                    "facilisis at iaculis eget, viverra ac sem. Aenean sagittis diam id aliquet condimentum. " +
                    "Integer accumsan fringilla scelerisque. Duis consectetur pharetra nibh nec faucibus. " +
                    "Praesent consequat aliquam finibus. Fusce quis ante nec mi cursus facilisis. "
        }.also{ database.issueReports.add(it) }
        val issue = database.issueReports.first { it.id eq newIssue.id }
        assertEquals(newIssue.id, issue.id)
        assertEquals(newIssue.room.id, issue.room.id)
        assertEquals(newIssue.description, issue.description)
        newIssue.description += "Veni Vini Vici."
        newIssue.flushChanges()
        val changedIssue = database.issueReports.first { it.id eq newIssue.id }
        assertEquals(newIssue.id, changedIssue.id)
        assertEquals(newIssue.room.id, changedIssue.room.id)
        assertEquals(newIssue.description, changedIssue.description)
    }

    @Test
    fun `Should delete an issue`(){
        val newRoom = Room {
            capacity = 15
        }.also{ database.rooms.add(it) }
        val newIssue = IssueReport {
            room = newRoom
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean efficitur ex ac dui dictum " +
                    "blandit. Morbi gravida eget nunc at viverra. Donec maximus nisl sit amet nibh dignissim, " +
                    "non fermentum tellus luctus. In laoreet libero lacinia tellus finibus placerat. Integer vitae " +
                    "sagittis sapien. Aenean et iaculis sapien, a finibus quam. Sed id lacus id mauris dictum " +
                    "vestibulum quis non lacus. Nunc aliquam fringilla turpis vitae semper. Praesent nisl diam, " +
                    "facilisis at iaculis eget, viverra ac sem. Aenean sagittis diam id aliquet condimentum. " +
                    "Integer accumsan fringilla scelerisque. Duis consectetur pharetra nibh nec faucibus. " +
                    "Praesent consequat aliquam finibus. Fusce quis ante nec mi cursus facilisis. "
        }.also{ database.issueReports.add(it) }
        newIssue.delete()
        assertNull(database.issueReports.firstOrNull { it.id eq newIssue.id })
    }
}