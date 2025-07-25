package isel.leic.group25.db.repositories.rooms.interfaces

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.rooms.OfficeRoom
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.entities.types.RoomType
import isel.leic.group25.db.entities.users.Teacher

interface RoomRepositoryInterface {
    fun getAllRooms(): List<Room>
    fun getAllRooms(limit: Int, offset: Int): List<Room>
    fun getAllRoomsByUniversityId(universityId: Int): List<Room>
    fun getAllRoomsByUniversityId(universityId: Int, limit: Int, offset: Int): List<Room>
    fun getAllRoomsByNameAndUniversityId(universityId: Int, roomPartialName: String, limit: Int, offset: Int): List<Room>
    fun getAllRoomsByNameByTypeAndUniversityId(universityId: Int, roomPartialName: String, roomType: RoomType, limit: Int, offset: Int): List<Room>
    fun getAllRoomsByUniversityIdAndType(universityId: Int, roomType: RoomType, limit: Int, offset: Int): List<Room>
    fun getRoomById(id: Int): Room?
    fun getOfficeRoomById(id: Int): OfficeRoom?
    fun getClassRoomById(id: Int): Classroom?
    fun createRoom(capacity: Int, name: String, university: University): Room
    fun createClassRoom(room: Room): Int
    fun createOfficeRoom(room: Room): Int
    fun createStudyRoom(room: Room): Int
    fun deleteRoom(id: Int): Boolean
    fun updateRoom(room:Room, name: String, capacity: Int): Room
    fun addTeacherToOffice(teacher: Teacher, office: OfficeRoom): Teacher
    fun removeTeacherFromOffice(teacher: Teacher, office: OfficeRoom) : Teacher
}