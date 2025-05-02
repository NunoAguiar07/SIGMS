package services

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.types.RoomType
import isel.leic.group25.services.RoomService
import isel.leic.group25.services.errors.RoomError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.rooms.MockRoomRepository
import mocks.repositories.utils.MockTransaction
import repositories.DatabaseTestSetup
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RoomServiceTest {
    private val roomRepository = MockRoomRepository()
    private val transactionInterface = MockTransaction()

    private val roomService = RoomService(roomRepository, transactionInterface)

    private fun createTestRooms(count: Int = 1): List<Room> {
        return transactionInterface.useTransaction {
            (1..count).map { i ->
                roomRepository.createRoom(10, "Test Room $i")
            }
        }
    }

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
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
        val result = roomService.createRoom(10, "Class Room 1", RoomType.CLASS)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("Class Room 1", successResult.name, "Expected room name to match")
        assertEquals(10, successResult.capacity, "Expected room capacity to match")
    }

    @Test
    fun `createRoom succeeds with valid parameters for office room`() {
        val result = roomService.createRoom(10, "Office Room 1", RoomType.OFFICE)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("Office Room 1", successResult.name, "Expected room name to match")
        assertEquals(10, successResult.capacity, "Expected room capacity to match")
    }

    @Test
    fun `createRoom succeeds with valid parameters for study room`() {
        val result = roomService.createRoom(10, "Study Room 1", RoomType.STUDY)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("Study Room 1", successResult.name, "Expected room name to match")
        assertEquals(10, successResult.capacity, "Expected room capacity to match")
    }

}