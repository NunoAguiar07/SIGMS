package repositories.rooms

import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import repositories.DatabaseTestSetup
import repositories.DatabaseTestSetup.Companion.database
import kotlin.test.AfterTest
import kotlin.test.Test



class RoomRepositoryTest {
    private val kTormCommand = KtormCommand(database)
    private val roomRepository = RoomRepository(database)
    private val universityRepository = UniversityRepository(database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `Should create a new room and find it by id`() {
        kTormCommand.useTransaction {
            val createUniversity = universityRepository.createUniversity("testUniversity")
            val newRoom = roomRepository.createRoom(20, "testRoom", createUniversity)
            val foundRoom = roomRepository.getRoomById(newRoom.id)
            assert(foundRoom != null)
            assert(foundRoom?.name == newRoom.name)
        }
    }

    @Test
    fun `Should create a new room and find it by name`() {
        kTormCommand.useTransaction {
            val createUniversity = universityRepository.createUniversity("testUniversity")
            val newRoom = roomRepository.createRoom(20, "testRoom", createUniversity)
            val foundRoom = roomRepository.getAllRooms().firstOrNull { it.name == newRoom.name }
            assert(foundRoom != null)
            assert(foundRoom?.name == newRoom.name)
        }
    }

    @Test
    fun `Should get all rooms`() {
        kTormCommand.useTransaction {
            val createUniversity = universityRepository.createUniversity("testUniversity")
            val newRoom1 = roomRepository.createRoom(20, "testRoom1", createUniversity)
            val newRoom2 = roomRepository.createRoom(30, "testRoom2", createUniversity)
            val rooms = roomRepository.getAllRooms(10, 0)
            assert(rooms.size == 2)
            assert(rooms.contains(newRoom1))
            assert(rooms.contains(newRoom2))
        }
    }

    @Test
    fun `Should create a new classroom and find it by id`() {
        kTormCommand.useTransaction {
            val createUniversity = universityRepository.createUniversity("testUniversity")
            val newRoom = roomRepository.createRoom(30, "testRoom5", createUniversity)
            roomRepository.createClassRoom(newRoom)
            val foundClassroom = roomRepository.getAllRooms().firstOrNull()
            assert(foundClassroom != null)
            assert(foundClassroom?.name == newRoom.name)
        }
    }
    @Test
    fun `Should create a new office room and find it by id`() {
        kTormCommand.useTransaction {
            val createUniversity = universityRepository.createUniversity("testUniversity")
            val newRoom = roomRepository.createRoom(40, "testRoom6", createUniversity)
            roomRepository.createOfficeRoom(newRoom)
            val foundOfficeRoom = roomRepository.getAllRooms().firstOrNull()
            assert(foundOfficeRoom != null)
            assert(foundOfficeRoom?.name == newRoom.name)
        }
    }
    @Test
    fun `Should create a new study room and find it by id`() {
        kTormCommand.useTransaction {
            val createUniversity = universityRepository.createUniversity("testUniversity")
            val newRoom = roomRepository.createRoom(45, "testRoom7", createUniversity)
            roomRepository.createStudyRoom(newRoom)
            val foundStudyRoom = roomRepository.getAllRooms().firstOrNull()
            assert(foundStudyRoom != null)
            assert(foundStudyRoom?.name == newRoom.name)
        }
    }

}