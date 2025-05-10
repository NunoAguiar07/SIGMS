package isel.leic.group25.services

import UniversityRepositoryInterface
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.types.RoomType
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.rooms.interfaces.RoomRepositoryInterface
import isel.leic.group25.services.errors.RoomError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import java.sql.SQLException

typealias RoomListResult = Either<RoomError, List<Room>>

typealias RoomResult = Either<RoomError, Room>

typealias DeleteRoomResult = Either<RoomError, Boolean>

class RoomService (
    private val roomRepository: RoomRepositoryInterface,
    private val universityRepository: UniversityRepositoryInterface,
    private val transactionInterface: TransactionInterface,
) {
    private inline fun <T> runCatching(block: () -> Either<RoomError, T>): Either<RoomError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(RoomError.ConnectionDbError(e.message))
        }
    }

    fun getAllRooms(limit: Int, offset: Int): RoomListResult {
        return runCatching {
            transactionInterface.useTransaction {
                val rooms = roomRepository.getAllRooms(limit, offset)
                return@useTransaction success(rooms)
            }
        }
    }

    fun getAllRoomsByUniversityId(universityId: Int, limit: Int, offset: Int): RoomListResult {
        return runCatching {
            transactionInterface.useTransaction {
                val university = universityRepository.getUniversityById(universityId)
                ?: return@useTransaction failure(RoomError.UniversityNotFound)
                val rooms = roomRepository.getAllRoomsByUniversityId(university.id, limit, offset)
                return@useTransaction success(rooms)
            }
        }
    }

    fun getRoomById(id: Int): RoomResult {
        return runCatching {
            transactionInterface.useTransaction {
                val room = roomRepository.getRoomById(id) ?: return@useTransaction failure(RoomError.RoomNotFound)
                return@useTransaction success(room)
            }
        }
    }

    fun createRoom(capacity: Int, name: String, universityId: Int, type: RoomType): RoomResult {
        return runCatching {
            transactionInterface.useTransaction {
                val university = universityRepository.getUniversityById(universityId)
                    ?: return@useTransaction failure(RoomError.UniversityNotFound)
                val room = roomRepository.createRoom(capacity, name, university)
                when (type) {
                    RoomType.CLASS -> roomRepository.createClassRoom(room)
                    RoomType.OFFICE -> roomRepository.createOfficeRoom(room)
                    RoomType.STUDY -> roomRepository.createStudyRoom(room)
                }
                return@useTransaction success(room)
            }
        }
    }
    fun deleteRoom(id: Int): DeleteRoomResult {
        return runCatching {
            transactionInterface.useTransaction {
                val room = roomRepository.getRoomById(id) ?: return@useTransaction failure(RoomError.RoomNotFound)
                if (roomRepository.deleteRoom(room.id)) {
                    return@useTransaction success(true)
                }
                return@useTransaction failure(RoomError.ConnectionDbError("Failed to delete room with id $id"))
            }
        }
    }

    fun updateRoom(id: Int, name: String, capacity: Int): RoomResult {
        return runCatching {
            transactionInterface.useTransaction {
                val room = roomRepository.getRoomById(id) ?: return@useTransaction failure(RoomError.RoomNotFound)
                val updatedRoom = roomRepository.updateRoom(room, name, capacity)
                return@useTransaction success(updatedRoom)
            }
        }
    }


}