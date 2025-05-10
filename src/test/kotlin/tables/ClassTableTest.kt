package tables

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.tables.Tables.Companion.classes
import isel.leic.group25.db.tables.Tables.Companion.subjects
import isel.leic.group25.db.tables.Tables.Companion.universities
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.first
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.update
import java.io.StringReader
import java.sql.Connection
import javax.sql.DataSource
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ClassTableTest {
    private val connection: Connection
    private val database: Database

    @AfterTest
    fun clearDB(){
        RunScript.execute(connection, StringReader("""
            DELETE FROM UNIVERSITY;
            DELETE FROM SUBJECT;
            DELETE FROM CLASS;
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
            
            CREATE TABLE IF NOT EXISTS CLASS (
                id SERIAL PRIMARY KEY,
                subject_id INT NOT NULL REFERENCES SUBJECT(id) ON DELETE CASCADE,
                class_name VARCHAR(255) NOT NULL
            );
        """)
        )
    }

    @Test
    fun `Should create a new class`(){
        val newUniversity = University{
            name = "ISEL"
        }.also { database.universities.add(it) }
        val newSubject = Subject{
            name = "PS"
            university = newUniversity
        }
        database.subjects.add(newSubject)
        val newClass = Class{
            subject = newSubject
            name = "51D"
        }

        database.classes.add(newClass)
        val retrievedClass = database.classes.first { it.id eq newClass.id }
        assertEquals(retrievedClass.id, newClass.id)
        assertEquals(retrievedClass.subject.id, newClass.subject.id)
    }

    @Test
    fun `Should update a class`(){
        val newUniversity = University{
            name = "ISEL"
        }.also { database.universities.add(it) }
        val newSubject = Subject{
            name = "PS"
            university = newUniversity
        }
        database.subjects.add(newSubject)
        val newClass = Class{
            subject = newSubject
            name = "51D"
        }
        database.classes.add(newClass)
        newClass.name = "51N"
        val rows = database.classes.update(newClass)
        assertEquals(1, rows)
    }

    @Test
    fun `Should delete a class`(){
        val newUniversity = University{
            name = "ISEL"
        }.also { database.universities.add(it) }
        val newSubject = Subject{
            name = "PS"
            university = newUniversity
        }
        database.subjects.add(newSubject)
        val newClass = Class{
            subject = newSubject
            name = "51D"
        }
        database.classes.add(newClass)
        newClass.delete()
        assertNull(database.classes.firstOrNull { it.id eq newClass.id })
    }
}