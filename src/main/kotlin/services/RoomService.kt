package isel.leic.group25.services

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.types.RoomType
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.rooms.interfaces.RoomRepositoryInterface
import isel.leic.group25.services.errors.RoomError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success

typealias RoomListResult = Either<RoomError, List<Room>>

typealias RoomResult = Either<RoomError, Room>

class RoomService (
    private val roomRepository: RoomRepositoryInterface,
    private val transactionInterface: TransactionInterface,
) {
    fun getAllRooms(limit: Int, offset: Int): RoomListResult {
        return transactionInterface.useTransaction {
            val rooms = roomRepository.getAllRooms(limit, offset)
            return@useTransaction success(rooms)
        }
    }

    fun getRoomById(id: Int): RoomResult {
        return transactionInterface.useTransaction {
            val room = roomRepository.getRoomById(id) ?: return@useTransaction failure(RoomError.RoomNotFound)
            return@useTransaction success(room)
        }
    }

    fun createRoom(capacity: Int, name: String, type: RoomType): RoomResult {
        return transactionInterface.useTransaction {
            val room = roomRepository.createRoom(capacity, name)
            when (type) {
                RoomType.CLASS -> roomRepository.createClassRoom(room)
                RoomType.OFFICE -> roomRepository.createOfficeRoom(room)
                RoomType.STUDY -> roomRepository.createStudyRoom(room)
            }
            return@useTransaction success(room)
        }
    }
    fun deleteRoom(id: Int): RoomResult {
        return transactionInterface.useTransaction {
            val room = roomRepository.getRoomById(id) ?: return@useTransaction failure(RoomError.RoomNotFound)
            if (roomRepository.deleteRoom(room.id)) {
                return@useTransaction success(room)
            }
            return@useTransaction failure(RoomError.RoomNotFound)
        }
    }

    fun updateRoom(id: Int, name: String, capacity: Int): RoomResult {
        return transactionInterface.useTransaction {
            val room = roomRepository.getRoomById(id) ?: return@useTransaction failure(RoomError.RoomNotFound)
            val updatedRoom = roomRepository.updateRoom(room, name, capacity)
            return@useTransaction success(updatedRoom)
        }
    }


}