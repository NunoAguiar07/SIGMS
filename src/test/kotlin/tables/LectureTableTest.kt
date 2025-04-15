package tables

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.tables.Tables.Companion.classes
import isel.leic.group25.db.tables.Tables.Companion.lectures
import isel.leic.group25.db.tables.Tables.Companion.rooms
import isel.leic.group25.db.tables.Tables.Companion.subjects
import isel.leic.group25.db.tables.timetables.Lectures
import isel.leic.group25.utils.toHoursAndMinutes
import junit.framework.TestCase.assertEquals
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.junit.jupiter.api.assertNull
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.update
import org.ktorm.entity.add
import org.ktorm.entity.first
import org.ktorm.entity.firstOrNull
import java.io.StringReader
import java.sql.Connection
import javax.sql.DataSource
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

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
                class_name VARCHAR(255) NOT NULL
            );
            
            CREATE TABLE IF NOT EXISTS ROOM (
                id SERIAL PRIMARY KEY,
                room_name VARCHAR(255) NOT NULL,
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
                class_type VARCHAR(20) CHECK (class_type IN ('theoretical', 'practical', 'theoretical_practical' )),
                week_day int CHECK(week_day > 0 and week_day < 8),
                start_time int NOT NULL,
                end_time int NOT NULL,
                CHECK (end_time > start_time)
            );
        """)
        )
    }

    @Test
    fun `Should create a new lecture`(){
        val newSubject = Subject{
            name = "PS"
        }.also { database.subjects.add(it) }
        val newClass = Class{
            subject = newSubject
            name = "51D"
        }.also { database.classes.add(it) }
        val newRoom = Room{
            name = "G.2.02"
            capacity = 15
        }.also{
            database.rooms.add(it)
        }
        val newLecture = Lecture {
            schoolClass = newClass
            room = newRoom
            type = ClassType.PRACTICAL
            weekDay = WeekDay.MONDAY
            startTime = Duration.parse("11h30m")
            endTime = Duration.parse("11h30m") + 90.minutes
        }.also { database.lectures.add(it) }
        val lecture = database.lectures.first { it.classId eq newLecture.schoolClass.id }
        assertEquals(newLecture.schoolClass.id, lecture.schoolClass.id)
        assertEquals(newLecture.room.id, lecture.room.id)
        assertEquals(newLecture.duration, lecture.duration)
        assertEquals(newLecture.weekDay, lecture.weekDay)
        assertEquals(newLecture.startTime.toHoursAndMinutes(), "11:30")
        assertEquals(newLecture.endTime.toHoursAndMinutes(), "13:00")
    }

    @Test
    fun `Should update a lecture`(){
        val newSubject = Subject{
            name = "PS"
        }.also { database.subjects.add(it) }
        val newClass = Class{
            subject = newSubject
            name = "51D"
        }.also { database.classes.add(it) }
        val newRoom = Room{
            name = "G.2.02"
            capacity = 15
        }.also{
            database.rooms.add(it)
        }
        val newLecture = Lecture {
            schoolClass = newClass
            room = newRoom
            type = ClassType.PRACTICAL
            startTime = Duration.parse("11h30m")
            endTime = Duration.parse("11h30m") + 90.minutes
        }.also { database.lectures.add(it) }
        val lecture = database.lectures.first { it.classId eq newLecture.schoolClass.id }
        assertEquals(newLecture.schoolClass.id, lecture.schoolClass.id)
        assertEquals(newLecture.room.id, lecture.room.id)
        assertEquals(newLecture.duration, lecture.duration)
        newLecture.endTime += Duration.parse("1h")
        newRoom.capacity = 20
        newLecture.type = ClassType.THEORETICAL
        database.update(Lectures) {
            set(it.endTime, newLecture.endTime)
            where {
                it.classId eq newLecture.schoolClass.id
            }
        }
        newRoom.flushChanges()
        newClass.flushChanges()
        val changedLecture = database.lectures.first { it.classId eq newLecture.schoolClass.id }
        assertEquals(newLecture.schoolClass.id, changedLecture.schoolClass.id)
        assertEquals(newLecture.room.id, changedLecture.room.id)
        assertEquals(newLecture.room.capacity, changedLecture.room.capacity)
        assertEquals(20, changedLecture.room.capacity)
        assertEquals(newLecture.duration, changedLecture.duration)
        assertEquals(Duration.parse("2h 30m"), changedLecture.duration)
    }

    @Test
    fun `Should delete a lecture`(){
        val newSubject = Subject{
            name = "PS"
        }.also { database.subjects.add(it) }
        val newClass = Class{
            subject = newSubject
            name = "51D"
        }.also { database.classes.add(it) }
        val newRoom = Room{
            name = "G.2.02"
            capacity = 15
        }.also{
            database.rooms.add(it)
        }
        val newLecture = Lecture {
            schoolClass = newClass
            room = newRoom
            type = ClassType.PRACTICAL
            startTime = Duration.parse("11h30m")
            endTime = Duration.parse("11h30m") + 90.minutes
        }.also { database.lectures.add(it) }
        database.delete(Lectures) {
            it.classId eq newLecture.schoolClass.id
        }
        assertNull( database.lectures.firstOrNull { it.classId eq newLecture.schoolClass.id } )
    }
}