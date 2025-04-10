package tables

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.tables.Tables.Companion.classes
import isel.leic.group25.db.tables.Tables.Companion.subjects
import kotlinx.datetime.Instant
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.junit.jupiter.api.assertThrows
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.first
import org.ktorm.entity.firstOrNull
import java.io.StringReader
import java.lang.Exception
import java.sql.Connection
import javax.sql.DataSource
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration

class ClassTableTest {
    private val connection: Connection
    private val database: Database

    @AfterTest
    fun clearDB(){
        RunScript.execute(connection, StringReader("""
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
            CREATE TABLE IF NOT EXISTS SUBJECT (
                 id SERIAL PRIMARY KEY,
                 subject_name VARCHAR(255) NOT NULL
            );
            
            CREATE TABLE IF NOT EXISTS CLASS (
                id SERIAL PRIMARY KEY,
                subject_id INT NOT NULL REFERENCES SUBJECT(id) ON DELETE CASCADE,
                class_name VARCHAR(255) NOT NULL,
                class_type VARCHAR(20) CHECK (class_type IN ('theoretical', 'practical')),
                start_time bigint NOT NULL,
                end_time bigint NOT NULL,
                CHECK (end_time > start_time)
            );
        """)
        )
    }

    @Test
    fun `Should create a new class`(){
        val newSubject = Subject{
            name = "PS"
        }
        database.subjects.add(newSubject)
        val currentTime = System.currentTimeMillis()
        val newClass = Class{
            subject = newSubject
            name = "51D"
            type = ClassType.PRACTICAL
            startTime = Instant.fromEpochMilliseconds(currentTime)
            endTime = Instant.fromEpochMilliseconds(currentTime + 3600*1000)
        }

        assertEquals(newClass.duration, Duration.parse("1h"))
        database.classes.add(newClass)
        val retrievedClass = database.classes.first { it.id eq newClass.id }
        assertEquals(retrievedClass.id, newClass.id)
        assertEquals(retrievedClass.subject.id, newClass.subject.id)
        assertEquals(retrievedClass.type, newClass.type)
        assertEquals(retrievedClass.startTime, newClass.startTime)
        assertEquals(retrievedClass.endTime, newClass.endTime)
        assertEquals(retrievedClass.duration, newClass.duration)
    }

    @Test
    fun `Should update a class`(){
        val newSubject = Subject{
            name = "PS"
        }
        database.subjects.add(newSubject)
        val currentTime = System.currentTimeMillis()
        val newClass = Class{
            subject = newSubject
            name = "51D"
            type = ClassType.PRACTICAL
            startTime = Instant.fromEpochMilliseconds(currentTime)
            endTime = Instant.fromEpochMilliseconds(currentTime + 3600*1000)
        }
        database.classes.add(newClass)
        newClass.type = ClassType.THEORETICAL
        newClass.flushChanges()
        val retrievedClass = database.classes.first { it.id eq newClass.id }
        assertEquals(retrievedClass.type, newClass.type)
        assertEquals(retrievedClass.type, ClassType.THEORETICAL)
    }

    @Test
    fun `Should delete a class`(){
        val newSubject = Subject{
            name = "PS"
        }
        database.subjects.add(newSubject)
        val currentTime = System.currentTimeMillis()
        val newClass = Class{
            subject = newSubject
            name = "51D"
            type = ClassType.PRACTICAL
            startTime = Instant.fromEpochMilliseconds(currentTime)
            endTime = Instant.fromEpochMilliseconds(currentTime + 3600*1000)
        }
        database.classes.add(newClass)
        newClass.delete()
        assertNull(database.classes.firstOrNull { it.id eq newClass.id })
    }

    @Test
    fun `Should not allow an endTime smaller than startTime`(){
        val newSubject = Subject{
            name = "PS"
        }
        database.subjects.add(newSubject)
        val currentTime = System.currentTimeMillis()
        val newClass = Class{
            subject = newSubject
            name = "51D"
            type = ClassType.PRACTICAL
            startTime = Instant.fromEpochMilliseconds(currentTime)
            endTime = Instant.fromEpochMilliseconds(currentTime - 3600*1000)
        }
        assertThrows<Exception>{database.classes.add(newClass)}
    }
}