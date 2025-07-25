package mocks.repositories.rooms

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.rooms.OfficeRoom
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.rooms.StudyRoom
import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.entities.types.RoomType
import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.repositories.rooms.interfaces.RoomRepositoryInterface

class MockRoomRepository : RoomRepositoryInterface {
    private val rooms = mutableListOf<Room>()
    private val classrooms = mutableListOf<Classroom>()
    private val officeRooms = mutableListOf<OfficeRoom>()
    private val studyRooms = mutableListOf<StudyRoom>()

    override fun getAllRooms(): List<Room> = rooms.toList()

    override fun getAllRooms(limit: Int, offset: Int): List<Room> =
        rooms.drop(offset).take(limit).toList()

    override fun getAllRoomsByUniversityId(universityId: Int): List<Room> {
        return rooms.filter { it.university.id == universityId }
    }

    override fun getAllRoomsByUniversityId(universityId: Int, limit: Int, offset: Int): List<Room> {
        return rooms.filter { it.university.id == universityId }
            .drop(offset)
            .take(limit)
    }

    override fun getAllRoomsByNameByTypeAndUniversityId(
        universityId: Int,
        roomPartialName: String,
        roomType: RoomType,
        limit: Int,
        offset: Int
    ): List<Room> {
        return rooms.filter {
            it.university.id == universityId &&
                    it.name.contains(roomPartialName, ignoreCase = true) &&
                    classrooms.any { classroom -> classroom.room.id == it.id }
        }.drop(offset).take(limit)
    }

    override fun getAllRoomsByUniversityIdAndType(
        universityId: Int,
        roomType: RoomType,
        limit: Int,
        offset: Int
    ): List<Room> {
        return when (roomType) {
            RoomType.CLASS -> classrooms.filter { it.room.university.id == universityId }
                .map { it.room }.drop(offset).take(limit)
            RoomType.OFFICE -> officeRooms.filter { it.room.university.id == universityId }
                .map { it.room }.drop(offset).take(limit)
            RoomType.STUDY -> studyRooms.filter { it.room.university.id == universityId }
                .map { it.room }.drop(offset).take(limit)
            else -> emptyList()
        }
    }

    override fun getAllRoomsByNameAndUniversityId(universityId: Int, roomPartialName: String, limit: Int, offset: Int): List<Room> {
        return rooms.filter {
            it.university.id == universityId && it.name.contains(roomPartialName, ignoreCase = true)
        }.drop(offset).take(limit)
    }

    override fun getRoomById(id: Int): Room? =
        rooms.firstOrNull { it.id == id }

    override fun getOfficeRoomById(id: Int): OfficeRoom? =
        officeRooms.firstOrNull { it.room.id == id }

    override fun getClassRoomById(id: Int): Classroom? =
        classrooms.firstOrNull { it.room.id == id }

    override fun createRoom(capacity: Int, name: String, university: University): Room {
        val newRoom = Room {
            this.name = name
            this.capacity = capacity
            this.university = university
        }
        newRoom["id"] = rooms.size + 1
        rooms.add(newRoom)
        return newRoom
    }

    override fun createClassRoom(room: Room): Int {
        classrooms.add(Classroom { this.room = room })
        return 1
    }

    override fun createOfficeRoom(room: Room): Int {
        officeRooms.add(OfficeRoom { this.room = room })
        return 1
    }

    override fun createStudyRoom(room: Room): Int {
        studyRooms.add(StudyRoom { this.room = room })
        return 1
    }

    override fun deleteRoom(id: Int): Boolean {
        rooms.firstOrNull { it.id == id } ?: return false
        classrooms.removeIf { it.room.id == id }
        officeRooms.removeIf { it.room.id == id }
        studyRooms.removeIf { it.room.id == id }
        rooms.removeIf { it.id == id }
        return true
    }

    override fun updateRoom(room: Room, name: String, capacity: Int): Room {
        room.name = name
        room.capacity = capacity
        return room
    }

    override fun addTeacherToOffice(teacher: Teacher, office: OfficeRoom): Teacher {
        teacher.office = office
        return teacher
    }

    override fun removeTeacherFromOffice(teacher: Teacher, office: OfficeRoom): Teacher {
        teacher.office = null
        return teacher
    }

    fun clear() {
        rooms.clear()
        classrooms.clear()
        officeRooms.clear()
        studyRooms.clear()
    }
}