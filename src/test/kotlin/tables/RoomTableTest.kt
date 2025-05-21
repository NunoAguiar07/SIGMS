package tables

import isel.leic.group25.db.tables.Tables.Companion.classrooms
import isel.leic.group25.db.tables.Tables.Companion.officeRooms
import isel.leic.group25.db.tables.Tables.Companion.rooms
import isel.leic.group25.db.tables.Tables.Companion.studyRooms
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.ktorm.dsl.eq
import org.ktorm.entity.firstOrNull
import kotlin.test.*

class RoomTableTest {
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
    fun `Should create a new room`() {
        val newRoom = dbHelper.createRoom(
            name = "G.2.02",
            capacity = 30
        )

        val room = database.rooms.firstOrNull { it.id eq newRoom.id }
        assertNotNull(room, "Room should not exist in database before adding")
        assertEquals(room.id, newRoom.id)
        assertEquals(room.capacity, newRoom.capacity)
    }

    @Test
    fun `Should update a room`() {
        val newRoom = dbHelper.createRoom(
            name = "G.2.02",
            capacity = 30
        )

        newRoom.capacity = 35
        newRoom.flushChanges()

        val room = database.rooms.firstOrNull { it.id eq newRoom.id }
        assertNotNull(room, "Room should exist in database after updating")
        assertEquals(newRoom.id, room.id)
        assertEquals(35, room.capacity)
    }

    @Test
    fun `Should delete a room`() {
        val newRoom = dbHelper.createRoom(
            name = "G.2.02",
            capacity = 30
        )
        val initialRoom = database.rooms.firstOrNull { it.id eq newRoom.id }
        assertNotNull(initialRoom, "Room should exist in database before deleting")

        newRoom.delete()

        assertNull(database.rooms.firstOrNull { it.id eq initialRoom.id })
    }

    @Test
    fun `Should not allow a room with less than 1 capacity`() {
        assertDoesNotThrow {
            dbHelper.createRoom(
                name = "ValidRoom",
                capacity = 10
            )
        }
        assertThrows<Exception> {
            dbHelper.createRoom(
            name = "Invalid2",
            capacity = 0
            )
        }
        assertThrows<Exception> {
            dbHelper.createRoom(
            name = "Invalid1",
            capacity = -5
            )
        }
    }

    @Test
    fun `Should create a new study room`() {
        val newRoom = dbHelper.createRoom(
            name = "StudyRoom1",
            capacity = 20,
            type = "STUDY_ROOM"
        )

        val studyRoom = database.studyRooms.firstOrNull { it.id eq newRoom.id }
        assertNotNull(studyRoom, "Study room should exist in database")
        assertEquals(newRoom.id, studyRoom.room.id)
        assertEquals(20, studyRoom.room.capacity)
    }

    @Test
    fun `Should create a new classroom`() {
        val newRoom = dbHelper.createRoom(
            name = "Classroom1",
            capacity = 30,
            type = "CLASSROOM"
        )

        val classroom = database.classrooms.firstOrNull { it.id eq newRoom.id }
        assertNotNull(classroom, "Classroom should exist in database")
        assertEquals(newRoom.id, classroom.room.id)
        assertEquals(30, classroom.room.capacity)
    }

    @Test
    fun `Should create a new office room`() {
        val newRoom = dbHelper.createRoom(
            name = "Office1",
            capacity = 5,
            type = "OFFICE_ROOM"
        )

        val officeRoom = database.officeRooms.firstOrNull { it.id eq newRoom.id }
        assertNotNull(officeRoom, "Office room should exist in database")
        assertEquals(newRoom.id, officeRoom.room.id)
        assertEquals(5, officeRoom.room.capacity)
    }

    @Test
    fun `Should enforce unique room names per university`() {
        dbHelper.createRoom(
            name = "UniqueRoom",
            capacity = 10
        )

        assertThrows<Exception> {
            dbHelper.createRoom(
                name = "UniqueRoom",
                capacity = 15
            )
        }

        val otherUniversity = dbHelper.createUniversity("Other University")
        assertDoesNotThrow { dbHelper.createRoom(
            name = "UniqueRoom",
            capacity = 20,
            university = otherUniversity
        ) }
    }
}