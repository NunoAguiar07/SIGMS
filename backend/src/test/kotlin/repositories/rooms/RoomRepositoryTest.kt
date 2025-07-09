package repositories.rooms

import isel.leic.group25.db.entities.rooms.OfficeRoom
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.users.UserRepository
import isel.leic.group25.db.tables.Tables.Companion.teachers
import org.ktorm.entity.add
import repositories.DatabaseTestSetup
import repositories.DatabaseTestSetup.Companion.database
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class RoomRepositoryTest {
    private val kTormCommand = KtormCommand(database)
    private val roomRepository = RoomRepository(database)
    private val universityRepository = UniversityRepository(database)
    private val userRepository = UserRepository(database)

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
    @Test
    fun `Should only return rooms from specified university`() {
        kTormCommand.useTransaction {
            val uni1 = universityRepository.createUniversity("Uni1")
            val uni2 = universityRepository.createUniversity("Uni2")
            roomRepository.createRoom(10, "Room1", uni1)
            roomRepository.createRoom(10, "Room2", uni2)

            val uni1Rooms = roomRepository.getAllRoomsByUniversityId(uni1.id)
            assertEquals(1, uni1Rooms.size)
            assertEquals("Room1", uni1Rooms.first().name)
        }
    }
    @Test
    fun `Should return rooms by partial name and university`() {
        kTormCommand.useTransaction {
            val uni = universityRepository.createUniversity("TestUni")
            roomRepository.createRoom(10, "Library Room", uni)
            roomRepository.createRoom(10, "Meeting Room", uni)
            roomRepository.createRoom(10, "Other", uni)

            val results = roomRepository.getAllRoomsByNameAndUniversityId(uni.id, "Room", 10, 0)
            assertEquals(2, results.size)
            assert(results.all { it.name.contains("Room") })
        }
    }
    @Test
    fun `Should delete a room and confirm it's gone`() {
        kTormCommand.useTransaction {
            val uni = universityRepository.createUniversity("Uni")
            val room = roomRepository.createRoom(20, "ToDelete", uni)

            val deleted = roomRepository.deleteRoom(room.id)
            val found = roomRepository.getRoomById(room.id)
            assert(deleted)
            assert(found == null)
        }
    }
    @Test
    fun `Should update room name and capacity`() {
        kTormCommand.useTransaction {
            val uni = universityRepository.createUniversity("Uni")
            val room = roomRepository.createRoom(15, "OldName", uni)

            val updated = roomRepository.updateRoom(room, "NewName", 30)

            assertEquals("NewName", updated.name)
            assertEquals(30, updated.capacity)
        }
    }
    @Test
    fun `Should create and fetch specific room types by id`() {
        kTormCommand.useTransaction {
            val uni = universityRepository.createUniversity("Uni")
            val classRoom = roomRepository.createRoom(25, "Class1", uni)
            roomRepository.createClassRoom(classRoom)

            val fetched = roomRepository.getClassRoomById(classRoom.id)
            assertNotNull(fetched)
            assertEquals(classRoom.id, fetched.room.id)
        }
    }

    @Test
    fun `Should assign and remove teacher from office`() {
        kTormCommand.useTransaction {
            val uni = universityRepository.createUniversity("Uni")

            val user = userRepository.createWithRole(
                email = "teacher@test.com",
                username = "TeacherX",
                password = User.hashPassword("secret"),
                role = Role.TEACHER,
                university = uni,
                authProvider = "local"
            )
            val teacher = user.toTeacher(database)

            val room = roomRepository.createRoom(10, "Office1", uni)
            roomRepository.createOfficeRoom(room)

            val assigned = roomRepository.addTeacherToOffice(teacher, room.toOfficeRoom(database))
            assertEquals(room.id, assigned.office?.room?.id  )

            val removed = roomRepository.removeTeacherFromOffice(assigned, room.toOfficeRoom(database))
            assertEquals(null, removed.office)
        }
    }







}