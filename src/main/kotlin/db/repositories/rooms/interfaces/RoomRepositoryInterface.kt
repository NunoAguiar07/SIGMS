package isel.leic.group25.db.repositories.rooms.interfaces

import isel.leic.group25.db.entities.rooms.Room

interface RoomRepositoryInterface {
    fun getAllRooms(): List<Room>
    fun getAllRooms(limit: Int, offset: Int): List<Room>
    fun getRoomById(id: Int): Room?
    fun createRoom(capacity: Int, name: String): Room?
    fun createClassRoom(room: Room): Int
    fun createOfficeRoom(room: Room): Int
    fun createStudyRoom(room: Room): Int
}