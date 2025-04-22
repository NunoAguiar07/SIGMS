package repositories.rooms

import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.rooms.RoomRepository
import repositories.DatabaseTestSetup
import kotlin.test.AfterTest
import kotlin.test.Test



class RoomRepositoryTest {
    private val kTransaction = KTransaction(DatabaseTestSetup.database)
    private val roomRepository = RoomRepository(DatabaseTestSetup.database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `Should create a new room and find it by id`() {
        kTransaction.useTransaction {
            val newRoom = roomRepository.createRoom(20, "testRoom")
            val foundRoom = roomRepository.getRoomById(newRoom.id)
            assert(foundRoom != null)
            assert(foundRoom?.name == newRoom.name)
        }
    }

    @Test
    fun `Should create a new room and find it by name`() {
        kTransaction.useTransaction {
            val newRoom = roomRepository.createRoom(20, "testRoom")
            val foundRoom = roomRepository.getAllRooms().firstOrNull { it.name == newRoom.name }
            assert(foundRoom != null)
            assert(foundRoom?.name == newRoom.name)
        }
    }

    @Test
    fun `Should get all rooms`() {
        kTransaction.useTransaction {
            val newRoom1 = roomRepository.createRoom(20, "testRoom1")
            val newRoom2 = roomRepository.createRoom(30, "testRoom2")
            val rooms = roomRepository.getAllRooms(10, 0)
            assert(rooms.size == 2)
            assert(rooms.contains(newRoom1))
            assert(rooms.contains(newRoom2))
        }
    }

    @Test
    fun `Should create a new classroom and find it by id`() {
        kTransaction.useTransaction {
            val newRoom = roomRepository.createRoom(20, "testRoom")
            roomRepository.createClassRoom(newRoom)
            val foundClassroom = roomRepository.getAllRooms().firstOrNull()
            assert(foundClassroom != null)
            assert(foundClassroom?.name == newRoom.name)
        }
    }
    @Test
    fun `Should create a new office room and find it by id`() {
        kTransaction.useTransaction {
            val newRoom = roomRepository.createRoom(20, "testRoom")
            roomRepository.createOfficeRoom(newRoom)
            val foundOfficeRoom = roomRepository.getAllRooms().firstOrNull()
            assert(foundOfficeRoom != null)
            assert(foundOfficeRoom?.name == newRoom.name)
        }
    }
    @Test
    fun `Should create a new study room and find it by id`() {
        kTransaction.useTransaction {
            val newRoom = roomRepository.createRoom(20, "testRoom")
            roomRepository.createStudyRoom(newRoom)
            val foundStudyRoom = roomRepository.getAllRooms().firstOrNull()
            assert(foundStudyRoom != null)
            assert(foundStudyRoom?.name == newRoom.name)
        }
    }

}