import isel.leic.group25.db.entities.rooms.*
import isel.leic.group25.db.tables.Tables.Companion.classrooms
import isel.leic.group25.db.tables.Tables.Companion.officeRooms
import isel.leic.group25.db.tables.Tables.Companion.rooms
import isel.leic.group25.db.tables.Tables.Companion.studyRooms
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.junit.jupiter.api.assertDoesNotThrow
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

class RoomTableTest {
    private val connection: Connection
    private val database: Database

    @AfterTest
    fun clearDB(){
        RunScript.execute(connection, StringReader("""
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

            CREATE TABLE IF NOT EXISTS STUDY_ROOM (
                id SERIAL PRIMARY KEY REFERENCES ROOM(id) ON DELETE CASCADE
            );

            CREATE TABLE IF NOT EXISTS CLASSROOM (
                id SERIAL PRIMARY KEY REFERENCES ROOM(id) ON DELETE CASCADE
            );

            CREATE TABLE IF NOT EXISTS OFFICE_ROOM (
                id SERIAL PRIMARY KEY REFERENCES ROOM(id) ON DELETE CASCADE
            );
        """)
        )
    }

    @Test
    fun `Should create a new room`(){
        val newRoom = Room{
            capacity = 30
        }
        database.rooms.add(newRoom)
        val room = database.rooms.first { it.id eq newRoom.id }
        assertEquals(room.id, newRoom.id)
        assertEquals(room.capacity, newRoom.capacity)
    }

    @Test
    fun `Should update a room`(){
        val newRoom = Room{
            capacity = 30
        }
        database.rooms.add(newRoom)
        newRoom.capacity = 35
        newRoom.flushChanges()
        val room = database.rooms.first { it.id eq newRoom.id }
        assertEquals(newRoom.id, room.id)
        assertEquals(newRoom.capacity, room.capacity)
    }

    @Test
    fun `Should delete a room`(){
        val newRoom = Room{
            capacity = 30
        }
        database.rooms.add(newRoom)
        val room = database.rooms.first { it.id eq newRoom.id }
        assertEquals(newRoom.id, room.id)
        assertEquals(newRoom.capacity, room.capacity)
        newRoom.delete()
        val nullRoom = database.rooms.firstOrNull { it.id eq room.id }
        assertNull(nullRoom)
    }

    @Test
    fun `Should not allow a room with less than 1 capacity`(){
        val newRoom = Room{
            capacity = -1
        }
        assertThrows<Exception> { database.rooms.add(newRoom) }
        newRoom.capacity = 0
        assertThrows<Exception> { database.rooms.add(newRoom) }
        newRoom.capacity = 1
        assertDoesNotThrow { database.rooms.add(newRoom) }
    }

    @Test
    fun `Should create a new study room`(){
        val newRoom = Room{
            capacity = 30
        }
        val newStudyRoom = StudyRoom{
            room = newRoom
        }
        database.rooms.add(newRoom)
        database.studyRooms.add(newStudyRoom)
        val studyRoom = database.studyRooms.first { it.id eq newRoom.id }
        assertEquals(newStudyRoom.room.id, studyRoom.room.id)
        assertEquals(newStudyRoom.room.capacity, studyRoom.room.capacity)
    }

    @Test
    fun `Should create a new classroom`(){
        val newRoom = Room{
            capacity = 30
        }
        val newClassroom = Classroom{
            room = newRoom
        }
        database.rooms.add(newRoom)
        database.classrooms.add(newClassroom)
        val classroom = database.classrooms.first { it.id eq newRoom.id }
        assertEquals(newClassroom.room.id, classroom.room.id)
        assertEquals(newClassroom.room.capacity, classroom.room.capacity)
    }

    @Test
    fun `Should create a new office room`(){
        val newRoom = Room{
            capacity = 30
        }
        val newOfficeRoom = OfficeRoom{
            room = newRoom
        }
        database.rooms.add(newRoom)
        database.officeRooms.add(newOfficeRoom)
        val officeRoom = database.officeRooms.first { it.id eq newRoom.id }
        assertEquals(newOfficeRoom.room.id, officeRoom.room.id)
        assertEquals(newOfficeRoom.room.capacity, officeRoom.room.capacity)
    }
}