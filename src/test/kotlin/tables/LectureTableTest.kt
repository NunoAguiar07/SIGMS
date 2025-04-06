package tables

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.tables.Tables.Companion.classes
import isel.leic.group25.db.tables.Tables.Companion.lectures
import isel.leic.group25.db.tables.Tables.Companion.rooms
import isel.leic.group25.db.tables.Tables.Companion.subjects
import junit.framework.TestCase.assertEquals
import kotlinx.datetime.Instant
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.junit.jupiter.api.assertNull
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
import kotlin.time.Duration

class LectureTableTest {
    private val connection: Connection
    private val database: Database

    @AfterTest
    fun clearDB(){
        RunScript.execute(connection, StringReader("""
            DELETE FROM ROOM;
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
                class_type VARCHAR(20) CHECK (class_type IN ('theoretical', 'practical')),
                start_time bigint NOT NULL,
                end_time bigint NOT NULL,
                CHECK (end_time > start_time)
            );
            
            CREATE TABLE IF NOT EXISTS ROOM (
                id SERIAL PRIMARY KEY,
                capacity INT NOT NULL,
                CHECK(capacity > 0)
            );
            
            CREATE TABLE IF NOT EXISTS STUDY_ROOM (
                id SERIAL PRIMARY KEY REFERENCES ROOM(id) ON DELETE CASCADE
            );
            
            CREATE TABLE IF NOT EXISTS CLASSROOM (
                id SERIAL PRIMARY KEY REFERENCES ROOM(id) ON DELETE CASCADE
            );
            
            CREATE TABLE IF NOT EXISTS OFFICE_ROOM (
                id SERIAL PRIMARY KEY REFERENCES ROOM(id) ON DELETE CASCADE
            );
            
            CREATE TABLE IF NOT EXISTS LECTURE (
                id SERIAL PRIMARY KEY,
                class_id INT NOT NULL REFERENCES CLASS(id) ON DELETE CASCADE,
                room_id INT NOT NULL REFERENCES ROOM(id) ON DELETE CASCADE,
                duration VARCHAR(255) NOT NULL
            );
        """)
        )
    }

    @Test
    fun `Should create a new lecture`(){
        val newSubject = Subject{
            name = "PS"
        }.also { database.subjects.add(it) }
        val currentTime = System.currentTimeMillis()
        val newClass = Class{
            subject = newSubject
            type = ClassType.PRACTICAL
            startTime = Instant.fromEpochMilliseconds(currentTime)
            endTime = Instant.fromEpochMilliseconds(currentTime + 3600*1000)
        }.also { database.classes.add(it) }
        val newRoom = Room{
            capacity = 15
        }.also{
            database.rooms.add(it)
        }
        val newLecture = Lecture {
            schoolClass = newClass
            room = newRoom
            duration = newClass.duration
        }.also { database.lectures.add(it) }
        val lecture = database.lectures.first { it.id eq newLecture.id }
        assertEquals(newLecture.id, lecture.id)
        assertEquals(newLecture.schoolClass.id, lecture.schoolClass.id)
        assertEquals(newLecture.room.id, lecture.room.id)
        assertEquals(newLecture.duration, lecture.duration)
    }

    @Test
    fun `Should update a lecture`(){
        val newSubject = Subject{
            name = "PS"
        }.also { database.subjects.add(it) }
        val currentTime = System.currentTimeMillis()
        val newClass = Class{
            subject = newSubject
            type = ClassType.PRACTICAL
            startTime = Instant.fromEpochMilliseconds(currentTime)
            endTime = Instant.fromEpochMilliseconds(currentTime + 3600*1000)
        }.also { database.classes.add(it) }
        val newRoom = Room{
            capacity = 15
        }.also{
            database.rooms.add(it)
        }
        val newLecture = Lecture {
            schoolClass = newClass
            room = newRoom
            duration = newClass.duration
        }.also { database.lectures.add(it) }
        val lecture = database.lectures.first { it.id eq newLecture.id }
        assertEquals(newLecture.id, lecture.id)
        assertEquals(newLecture.schoolClass.id, lecture.schoolClass.id)
        assertEquals(newLecture.room.id, lecture.room.id)
        assertEquals(newLecture.duration, lecture.duration)
        newLecture.duration = newClass.duration + Duration.parse("1h")
        newRoom.capacity = 20
        newClass.type = ClassType.THEORETICAL
        newLecture.flushChanges()
        newRoom.flushChanges()
        newClass.flushChanges()
        val changedLecture = database.lectures.first { it.id eq newLecture.id }
        assertEquals(newLecture.id, changedLecture.id)
        assertEquals(newLecture.schoolClass.id, changedLecture.schoolClass.id)
        assertEquals(newLecture.schoolClass.type, changedLecture.schoolClass.type)
        assertEquals(ClassType.THEORETICAL, changedLecture.schoolClass.type)
        assertEquals(newLecture.room.id, changedLecture.room.id)
        assertEquals(newLecture.room.capacity, changedLecture.room.capacity)
        assertEquals(20, changedLecture.room.capacity)
        assertEquals(newLecture.duration, changedLecture.duration)
        assertEquals(Duration.parse("2h"), changedLecture.duration)
    }

    @Test
    fun `Should delete a lecture`(){
        val newSubject = Subject{
            name = "PS"
        }.also { database.subjects.add(it) }
        val currentTime = System.currentTimeMillis()
        val newClass = Class{
            subject = newSubject
            type = ClassType.PRACTICAL
            startTime = Instant.fromEpochMilliseconds(currentTime)
            endTime = Instant.fromEpochMilliseconds(currentTime + 3600*1000)
        }.also { database.classes.add(it) }
        val newRoom = Room{
            capacity = 15
        }.also{
            database.rooms.add(it)
        }
        val newLecture = Lecture {
            schoolClass = newClass
            room = newRoom
            duration = newClass.duration
        }.also { database.lectures.add(it) }
        newLecture.delete()
        assertNull( database.lectures.firstOrNull { it.id eq newLecture.id } )
    }
}