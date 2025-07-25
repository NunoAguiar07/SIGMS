package services

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.types.RoomType
import isel.leic.group25.services.RoomService
import isel.leic.group25.services.errors.RoomError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.MockRepositories
import org.ktorm.database.Database
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RoomServiceTest {
    private val mockDB = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        password = ""
    )
    private val mockRepositories = MockRepositories(mockDB)

    private val roomService = RoomService(mockRepositories, mockRepositories.ktormCommand)

    private fun createTestRooms(count: Int = 1): List<Room> {
        return mockRepositories.ktormCommand.useTransaction {
            (1..count).map { i ->
                val university = mockRepositories.from({universityRepository}){
                    createUniversity("Test University $i")
                }
                mockRepositories.from({roomRepository}){
                    createRoom(10, "Test Room $i", university)
                }
            }
        }
    }

    @Test
    fun `getAllRooms returns rooms with default parameters`() {
        val rooms = createTestRooms(5)
        val result = roomService.getAllRooms(10, 0)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertTrue(successResult.size == 5, "Expected 5 rooms")
        assertTrue(successResult.all { it in rooms }, "Expected all rooms to be in the result")
    }

    @Test
    fun `getAllRooms returns rooms with specified limit and offset`() {
        val rooms = createTestRooms(10)
        val result = roomService.getAllRooms(5, 2)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertTrue(successResult.size == 5, "Expected 5 rooms")
        assertTrue(successResult.all { it in rooms.subList(2, 7) }, "Expected rooms to match the specified range")
    }

    @Test
    fun `getRoomById returns room when exists`() {
        val room = createTestRooms(1)[0]
        val result = roomService.getRoomById(room.id)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(room, successResult, "Expected room to match")
    }

    @Test
    fun `getRoomById fails when room does not exist`() {
        val result = roomService.getRoomById(999)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            RoomError.RoomNotFound,
            result.value,
            "Expected RoomNotFound error"
        )
    }

    @Test
    fun `createRoom succeeds with valid parameters for class room`() {
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val result = roomService.createRoom(10, "Class Room 1", university.id, RoomType.CLASS)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("Class Room 1", successResult.name, "Expected room name to match")
        assertEquals(10, successResult.capacity, "Expected room capacity to match")
    }

    @Test
    fun `createRoom succeeds with valid parameters for office room`() {
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val result = roomService.createRoom(10, "Office Room 1", university.id, RoomType.OFFICE)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("Office Room 1", successResult.name, "Expected room name to match")
        assertEquals(10, successResult.capacity, "Expected room capacity to match")
    }

    @Test
    fun `createRoom succeeds with valid parameters for study room`() {
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val result = roomService.createRoom(10, "Study Room 1", university.id, RoomType.STUDY)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("Study Room 1", successResult.name, "Expected room name to match")
        assertEquals(10, successResult.capacity, "Expected room capacity to match")
    }
    @Test
    fun `createRoom fails when university does not exist`() {
        val result = roomService.createRoom(10, "Ghost Room", 999, RoomType.CLASS)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(RoomError.UniversityNotFound, result.value, "Expected UniversityNotFound")
    }

    @Test
    fun `deleteRoom succeeds for existing room`() {
        val room = createTestRooms(1)[0]
        val result = roomService.deleteRoom(room.id)
        assertTrue(result is Success, "Expected Success")
        assertTrue(result.value, "Expected true (room deleted)")

        // Double-check it’s gone
        val check = roomService.getRoomById(room.id)
        assertTrue(check is Failure, "Expected Failure after deletion")
        assertEquals(RoomError.RoomNotFound, check.value)
    }

    @Test
    fun `deleteRoom fails when room not found`() {
        val result = roomService.deleteRoom(999)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(RoomError.RoomNotFound, result.value)
    }

    @Test
    fun `updateRoom succeeds for existing room`() {
        val room = createTestRooms(1)[0]
        val result = roomService.updateRoom(room.id, "Updated Room", 20)
        assertTrue(result is Success, "Expected Success")
        val updated = result.value
        assertEquals("Updated Room", updated.name)
        assertEquals(20, updated.capacity)
    }

    @Test
    fun `updateRoom fails when room not found`() {
        val result = roomService.updateRoom(999, "Should Not Work", 10)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(RoomError.RoomNotFound, result.value)
    }

    @Test
    fun `getAllRoomsByUniversityId returns rooms of a university`() {
        val rooms = createTestRooms(3)
        val universityId = rooms[0].university.id
        val result = roomService.getAllRoomsByUniversityId(universityId, 10, 0)
        assertTrue(result is Success)
        assertTrue(result.value.isNotEmpty())
        assertTrue(result.value.all { it.university.id == universityId })
    }

    @Test
    fun `getRoomsByNameAndUniversityId returns matching rooms`() {
        val university = mockRepositories.from({universityRepository}) {
            createUniversity("Test University")
        }
        val room = mockRepositories.from({roomRepository}) {
            createRoom(20, "Unique Room Name", university)
        }
        val result = roomService.getRoomsByNameByTypeAndUniversityId(university.id, "Unique", null,10, 0)
        assertTrue(result is Success)
        assertEquals(1, result.value.size)
        assertEquals(room.id, result.value.first().id)
    }
}