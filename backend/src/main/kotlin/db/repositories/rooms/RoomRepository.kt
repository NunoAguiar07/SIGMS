package isel.leic.group25.db.repositories.rooms

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.rooms.OfficeRoom
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.rooms.StudyRoom
import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.entities.types.RoomType
import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.repositories.rooms.interfaces.RoomRepositoryInterface
import isel.leic.group25.db.repositories.utils.withDatabase
import isel.leic.group25.db.tables.Tables.Companion.classrooms
import isel.leic.group25.db.tables.Tables.Companion.officeRooms
import isel.leic.group25.db.tables.Tables.Companion.rooms
import isel.leic.group25.db.tables.Tables.Companion.studyRooms
import isel.leic.group25.db.tables.Tables.Companion.teachers
import isel.leic.group25.db.tables.rooms.OfficeRooms
import isel.leic.group25.db.tables.rooms.StudyRooms
import isel.leic.group25.db.tables.rooms.Rooms as RoomsTable
import isel.leic.group25.db.tables.rooms.Classrooms as ClassroomTable
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*

class RoomRepository (private val database: Database) : RoomRepositoryInterface {
    override fun getAllRooms(): List<Room> = withDatabase {
        return database.rooms.toList()
    }


    override fun getAllRooms(limit:Int, offset:Int): List<Room> = withDatabase {
        return database.rooms.drop(offset).take(limit).toList()
    }

    override fun getAllRoomsByUniversityId(universityId: Int): List<Room> {
        return database.rooms.filter { it.university eq universityId }.toList()
    }

    override fun getAllRoomsByUniversityId(universityId: Int, limit: Int, offset: Int): List<Room> {
        return database.rooms.filter { it.university eq universityId }.drop(offset).take(limit).toList()
    }

    override fun getAllRoomsByNameAndUniversityId(universityId: Int, roomPartialName: String, limit: Int, offset: Int): List<Room> {
        return database.from(RoomsTable)
            .select()
            .where {
                (RoomsTable.university eq universityId) and (RoomsTable.name like "%$roomPartialName%")
            }.limit(offset, limit).map { row ->
                RoomsTable.createEntity(row)
            }

    }


    override fun getAllRoomsByNameByTypeAndUniversityId(
        universityId: Int,
        roomPartialName: String,
        roomType: RoomType,
        limit: Int,
        offset: Int
    ): List<Room> {
        val query = when (roomType) {
            RoomType.CLASS -> database
                .from(ClassroomTable)
                .innerJoin(RoomsTable, on = ClassroomTable.id eq RoomsTable.id)
                .select()
                .where {
                    (RoomsTable.university eq universityId) and
                            (RoomsTable.name like "%$roomPartialName%")
                }

            RoomType.STUDY -> database
                .from(StudyRooms)
                .innerJoin(RoomsTable, on = StudyRooms.id eq RoomsTable.id)
                .select()
                .where {
                    (RoomsTable.university eq universityId) and
                            (RoomsTable.name like "%$roomPartialName%")
                }

            RoomType.OFFICE -> database
                .from(OfficeRooms)
                .innerJoin(RoomsTable, on = OfficeRooms.id eq RoomsTable.id)
                .select()
                .where {
                    (RoomsTable.university eq universityId) and
                            (RoomsTable.name like "%$roomPartialName%")
                }
        }

        return query
            .limit(offset, limit)
            .map { row -> RoomsTable.createEntity(row) }
    }

    override fun getAllRoomsByUniversityIdAndType(
        universityId: Int,
        roomType: RoomType,
        limit: Int,
        offset: Int
    ): List<Room> {
        val query = when (roomType) {
            RoomType.CLASS -> database
                .from(ClassroomTable)
                .innerJoin(RoomsTable, on = ClassroomTable.id eq RoomsTable.id)
                .select()
                .where { RoomsTable.university eq universityId }

            RoomType.STUDY -> database
                .from(StudyRooms)
                .innerJoin(RoomsTable, on = StudyRooms.id eq RoomsTable.id)
                .select()
                .where { RoomsTable.university eq universityId }

            RoomType.OFFICE -> database
                .from(OfficeRooms)
                .innerJoin(RoomsTable, on = OfficeRooms.id eq RoomsTable.id)
                .select()
                .where { RoomsTable.university eq universityId }
        }

        return query
            .limit(offset, limit)
            .map { row -> RoomsTable.createEntity(row) }
    }

    override fun getRoomById(id: Int): Room? = withDatabase {
        return database.rooms.firstOrNull { it.id eq id }
    }

    override fun getOfficeRoomById(id: Int): OfficeRoom? = withDatabase {
        return database.officeRooms.firstOrNull { it.id eq id }
    }

    override fun getClassRoomById(id: Int): Classroom? = withDatabase {
        return database.classrooms.firstOrNull { it.id eq id }
    }

    override fun createRoom(capacity: Int, name: String, university: University): Room = withDatabase {
        val newRoom = Room {
            this.name = name
            this.capacity = capacity
            this.university = university
        }
        database.rooms.add(newRoom)
        return newRoom
    }

    override fun createClassRoom(room: Room): Int = withDatabase {
        val newRoom = Classroom {
            this.room = room
        }
        return database.classrooms.add(newRoom)
    }

    override fun createOfficeRoom(room: Room): Int = withDatabase {
        val newRoom = OfficeRoom {
            this.room = room
        }
        return database.officeRooms.add(newRoom)
    }

    override fun createStudyRoom(room: Room): Int = withDatabase {
        val newRoom = StudyRoom {
            this.room = room
        }
        return database.studyRooms.add(newRoom)
    }

    override fun deleteRoom(id: Int): Boolean = withDatabase {
        val condition = database.rooms.removeIf { it.id eq id }
        return condition > 0
    }

    override fun updateRoom(room: Room, name: String, capacity: Int): Room = withDatabase {
        room.name = name
        room.capacity = capacity
        database.rooms.update(room)
        return room
    }

    override fun addTeacherToOffice(teacher: Teacher, office: OfficeRoom) : Teacher = withDatabase {
        teacher.office = office
        database.teachers.update(teacher)
        return teacher
    }

    override fun removeTeacherFromOffice(teacher: Teacher, office: OfficeRoom): Teacher = withDatabase {
        teacher.office = null
        database.teachers.update(teacher)
        return teacher
    }

}