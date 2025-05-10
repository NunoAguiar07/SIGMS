package tables

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.tables.Tables.Companion.subjects
import isel.leic.group25.db.tables.Tables.Companion.universities
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
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SubjectTableTest {
    private val connection: Connection
    private val database: Database

    @AfterTest
    fun clearDB(){
        RunScript.execute(connection, StringReader("""
            DELETE FROM UNIVERSITY;
            DELETE FROM SUBJECT;
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
            CREATE TABLE IF NOT EXISTS SUBJECT (
                 id SERIAL PRIMARY KEY,
                 subject_name VARCHAR(255) NOT NULL,
                 university_id INT NOT NULL REFERENCES UNIVERSITY(id) ON DELETE CASCADE
            );
        """)
        )
    }

    @Test
    fun `Should create a new subject`(){
        val newUniversity = University{
            name = "ISEL"
        }.also {
            database.universities.add(it)
        }
        val newSubject = Subject{
            name = "PS"
            university = newUniversity
        }

        database.subjects.add(newSubject)
        val subject = database.subjects.first{ it.id eq newSubject.id }
        assertEquals(subject.id, newSubject.id)
        assertEquals(subject.name, newSubject.name)
    }

    @Test
    fun `Should update a subject`(){
        val newUniversity = University{
            name = "ISEL"
        }.also {
            database.universities.add(it)
        }
        val newSubject = Subject{
            name = "PS"
            university = newUniversity
        }
        database.subjects.add(newSubject)
        newSubject.name = "PDM"
        newSubject.flushChanges()
        val subject = database.subjects.first{ it.id eq newSubject.id }
        assertEquals(subject.id, newSubject.id)
        assertEquals(subject.name, newSubject.name)
        assertEquals(subject.name, "PDM")
    }

    @Test
    fun `Should delete a subject`(){
        val newUniversity = University{
            name = "ISEL"
        }.also {
            database.universities.add(it)
        }
        val newSubject = Subject{
            name = "PS"
            university = newUniversity
        }
        database.subjects.add(newSubject)
        newSubject.delete()
        assertNull(database.subjects.firstOrNull { it.id eq newSubject.id })
    }
}