package isel.leic.group25.db.repositories.rooms

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.rooms.OfficeRoom
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.rooms.StudyRoom
import isel.leic.group25.db.repositories.rooms.interfaces.RoomRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.classrooms
import isel.leic.group25.db.tables.Tables.Companion.officeRooms
import isel.leic.group25.db.tables.Tables.Companion.rooms
import isel.leic.group25.db.tables.Tables.Companion.studyRooms
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*

class RoomRepository (private val database: Database) : RoomRepositoryInterface {
    override fun getAllRooms(): List<Room> {
        return database.rooms.toList()
    }

    override fun getAllRooms(limit:Int, offset:Int): List<Room> {
        return database.rooms.drop(offset).take(limit).toList()
    }

    override fun getRoomById(id: Int): Room? {
        return database.rooms.firstOrNull { it.id eq id }
    }

    override fun createRoom(capacity: Int, name: String): Room? {
        val newRoom = Room {
            this.name = name
            this.capacity = capacity
        }
        database.rooms.add(newRoom)
        return getRoomById(newRoom.id)
    }

    override fun createClassRoom(room: Room): Int {
        val newRoom = Classroom {
            this.room = room
        }
        return database.classrooms.add(newRoom)
    }

    override fun createOfficeRoom(room: Room): Int {
        val newRoom = OfficeRoom {
            this.room = room
        }
        return database.officeRooms.add(newRoom)
    }

    override fun createStudyRoom(room: Room): Int {
        val newRoom = StudyRoom {
            this.room = room
        }
        return database.studyRooms.add(newRoom)
    }

}