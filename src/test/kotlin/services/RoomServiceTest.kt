package services

import isel.leic.group25.db.entities.rooms.Room
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
        val result = roomService.getAllRooms(null, null)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertTrue(successResult.size == 5, "Expected 5 rooms")
        assertTrue(successResult.all { it in rooms }, "Expected all rooms to be in the result")
    }

    @Test
    fun `getAllRooms returns rooms with specified limit and offset`() {
        val rooms = createTestRooms(10)
        val result = roomService.getAllRooms("5", "2")
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertTrue(successResult.size == 5, "Expected 5 rooms")
        assertTrue(successResult.all { it in rooms.subList(2, 7) }, "Expected rooms to match the specified range")
    }

    @Test
    fun `getAllRooms fails with invalid limit`() {
        val result = roomService.getAllRooms("200", null)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            RoomError.InvalidRoomLimit,
            result.value,
            "Expected InvalidRoomLimit error"
        )
    }

    @Test
    fun `getAllRooms fails with invalid offset`() {
        val result = roomService.getAllRooms(null, "-1")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            RoomError.InvalidRoomOffSet,
            result.value,
            "Expected InvalidRoomOffSet error"
        )
    }

    @Test
    fun `getRoomById returns room when exists`() {
        val room = createTestRooms(1)[0]
        val result = roomService.getRoomById(room.id.toString())
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(room, successResult, "Expected room to match")
    }

    @Test
    fun `getRoomById fails with invalid ID`() {
        val result = roomService.getRoomById("invalid")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            RoomError.InvalidRoomId,
            result.value,
            "Expected InvalidRoomId error"
        )
    }

    @Test
    fun `getRoomById fails when room does not exist`() {
        val result = roomService.getRoomById("9999")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            RoomError.RoomNotFound,
            result.value,
            "Expected RoomNotFound error"
        )
    }

    @Test
    fun `createRoom succeeds with valid parameters for class room`() {
        val result = roomService.createRoom(10, "Class Room 1", "class")
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("Class Room 1", successResult.name, "Expected room name to match")
        assertEquals(10, successResult.capacity, "Expected room capacity to match")
    }

    @Test
    fun `createRoom succeeds with valid parameters for office room`() {
        val result = roomService.createRoom(10, "Office Room 1", "office")
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("Office Room 1", successResult.name, "Expected room name to match")
        assertEquals(10, successResult.capacity, "Expected room capacity to match")
    }

    @Test
    fun `createRoom succeeds with valid parameters for study room`() {
        val result = roomService.createRoom(10, "Study Room 1", "study")
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("Study Room 1", successResult.name, "Expected room name to match")
        assertEquals(10, successResult.capacity, "Expected room capacity to match")
    }

    @Test
    fun `createRoom fails with invalid capacity`() {
        val result = roomService.createRoom(-5, "Invalid Room", "class")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            RoomError.InvalidRoomCapacity,
            result.value,
            "Expected InvalidRoomCapacity error"
        )
    }

    @Test
    fun `createRoom fails with blank name`() {
        val result = roomService.createRoom(10, "", "class")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            RoomError.InvalidRoomData,
            result.value,
            "Expected InvalidRoomData error"
        )
    }

    @Test
    fun `createRoom fails with duplicate name`() {
        val room1 = createTestRooms(1)
        val result = roomService.createRoom(10, room1[0].name, "class")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            RoomError.RoomAlreadyExists,
            result.value,
            "Expected RoomAlreadyExists error"
        )
    }

    @Test
    fun `createRoom fails with invalid type`() {
        val result = roomService.createRoom(10, "Invalid Room", "invalid")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            RoomError.InvalidRoomType,
            result.value,
            "Expected InvalidRoomType error"
        )
    }

    @Test
    fun `createRoom fails with blank type`() {
        val result = roomService.createRoom(10, "Invalid Room", "")
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            RoomError.InvalidRoomType,
            result.value,
            "Expected InvalidRoomType error"
        )
    }

}