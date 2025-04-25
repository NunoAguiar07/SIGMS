package mocks.repositories.rooms

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.rooms.OfficeRoom
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.rooms.StudyRoom
import isel.leic.group25.db.repositories.rooms.interfaces.RoomRepositoryInterface

class MockRoomRepository : RoomRepositoryInterface {
    private val rooms = mutableListOf<Room>()
    private val classrooms = mutableListOf<Classroom>()
    private val officeRooms = mutableListOf<OfficeRoom>()
    private val studyRooms = mutableListOf<StudyRoom>()

    override fun getAllRooms(): List<Room> = rooms.toList()

    override fun getAllRooms(limit: Int, offset: Int): List<Room> =
        rooms.drop(offset).take(limit).toList()

    override fun getRoomById(id: Int): Room? =
        rooms.firstOrNull { it.id == id }

    override fun createRoom(capacity: Int, name: String): Room {
        val newRoom = Room {
            this.name = name
            this.capacity = capacity
        }
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
        val room = rooms.firstOrNull { it.id == id } ?: return false
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

    fun clear() {
        rooms.clear()
        classrooms.clear()
        officeRooms.clear()
        studyRooms.clear()
    }
}