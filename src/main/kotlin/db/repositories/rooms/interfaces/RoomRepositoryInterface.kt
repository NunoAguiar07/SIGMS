package isel.leic.group25.db.repositories.rooms.interfaces

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.rooms.OfficeRoom
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.users.Teacher

interface RoomRepositoryInterface {
    fun getAllRooms(): List<Room>
    fun getAllRooms(limit: Int, offset: Int): List<Room>
    fun getRoomById(id: Int): Room?
    fun getOfficeRoomById(id: Int): OfficeRoom?
    fun getClassRoomById(id: Int): Classroom?
    fun createRoom(capacity: Int, name: String): Room
    fun createClassRoom(room: Room): Int
    fun createOfficeRoom(room: Room): Int
    fun createStudyRoom(room: Room): Int
    fun deleteRoom(id: Int): Boolean
    fun updateRoom(room:Room, name: String, capacity: Int): Room
    fun addTeacherToOffice(teacher: Teacher, office: OfficeRoom): Teacher
    fun removeTeacherFromOffice(teacher: Teacher, office: OfficeRoom) : Teacher
}