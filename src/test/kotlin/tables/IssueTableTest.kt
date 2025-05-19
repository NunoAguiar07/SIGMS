package tables

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.tables.Tables.Companion.issueReports
import isel.leic.group25.db.tables.Tables.Companion.rooms
import isel.leic.group25.db.tables.Tables.Companion.universities
import isel.leic.group25.db.tables.Tables.Companion.users
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
            DELETE FROM UNIVERSITY;
            DELETE FROM ISSUE_REPORT;
            DELETE FROM ROOM;
            DELETE FROM USERS;
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
            CREATE TABLE IF NOT EXISTS UNIVERSITY (
                id SERIAL PRIMARY KEY,
                university_name VARCHAR(255) NOT NULL UNIQUE
            );
            CREATE TABLE IF NOT EXISTS USERS (
                id SERIAL PRIMARY KEY,
                email VARCHAR(255) UNIQUE NOT NULL,
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                profile_image VARCHAR(255),
                auth_provider VARCHAR(50) NOT NULL CHECK (auth_provider IN ('local', 'microsoft')),
                university_id INT NOT NULL REFERENCES UNIVERSITY(id) ON DELETE CASCADE
            );
            
            CREATE TABLE IF NOT EXISTS TECHNICAL_SERVICES (
                user_id INT UNIQUE NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
                primary key (user_id)
            );
           

            CREATE TABLE IF NOT EXISTS ROOM (
                id SERIAL PRIMARY KEY,
                room_name VARCHAR(255) NOT NULL,
                capacity INT NOT NULL,
                university_id INT NOT NULL REFERENCES UNIVERSITY(id) ON DELETE CASCADE,
                CHECK(capacity > 0),
                CONSTRAINT unique_room_per_university UNIQUE (room_name, university_id)
            );
            
            CREATE TABLE IF NOT EXISTS ISSUE_REPORT (
                id SERIAL PRIMARY KEY,
                room_id INT NOT NULL REFERENCES ROOM(id) ON DELETE CASCADE,
                created_by INT NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
                assigned_to INT DEFAULT NULL REFERENCES TECHNICAL_SERVICES(user_id) ON DELETE SET NULL,
                description TEXT NOT NULL
            );
        """)
        )
    }

    @Test
    fun `Should create a new issue`(){
        val newUniversity = University {
            name = "ISEL"
        }.also { database.universities.add(it) }
        val newRoom = Room {
            name = "G.0.02"
            capacity = 15
            university = newUniversity
        }.also{ database.rooms.add(it) }
        // Arrange
        val testEmail = "teste69mail@email.com"
        val testUsername = "jhondoe123"
        val testPassword = "supersecretpassword123"
        val newUser = User {
            email = testEmail
            username = testUsername
            password = User.hashPassword(testPassword)
            profileImage = byteArrayOf()
            authProvider = "local"
            university = newUniversity
        }
        database.users.add(newUser)
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
            createdBy = newUser
            assignedTo = null
        }.also{ database.issueReports.add(it) }
        val issue = database.issueReports.first { it.id eq newIssue.id }
        assertEquals(newIssue.id, issue.id)
        assertEquals(newIssue.room.id, issue.room.id)
        assertEquals(newIssue.description, newIssue.description)
    }

    @Test
    fun `Should update an issue`(){
        val newUniversity = University {
            name = "ISEL"
        }.also { database.universities.add(it) }
        val newRoom = Room {
            name = "G.0.02"
            capacity = 15
            university = newUniversity
        }.also{ database.rooms.add(it) }
        // Arrange
        val testEmail = "teste69mail@email.com"
        val testUsername = "jhondoe123"
        val testPassword = "supersecretpassword123"
        val newUser = User {
            email = testEmail
            username = testUsername
            password = User.hashPassword(testPassword)
            profileImage = byteArrayOf()
            authProvider = "local"
            university = newUniversity
        }
        database.users.add(newUser)
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
            createdBy = newUser
            assignedTo = null
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
        val newUniversity = University {
            name = "ISEL"
        }.also { database.universities.add(it) }
        val newRoom = Room {
            name = "G.0.02"
            capacity = 15
            university = newUniversity
        }.also{ database.rooms.add(it) }
        // Arrange
        val testEmail = "teste69mail@email.com"
        val testUsername = "jhondoe123"
        val testPassword = "supersecretpassword123"
        val newUser = User {
            email = testEmail
            username = testUsername
            password = User.hashPassword(testPassword)
            profileImage = byteArrayOf()
            authProvider = "local"
            university = newUniversity
        }
        database.users.add(newUser)
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
            createdBy = newUser
            assignedTo = null
        }.also{ database.issueReports.add(it) }
        newIssue.delete()
        assertNull(database.issueReports.firstOrNull { it.id eq newIssue.id })
    }
}