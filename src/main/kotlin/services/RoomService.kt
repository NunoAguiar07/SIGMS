package isel.leic.group25.services

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.services.errors.RoomError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success

typealias RoomListResult = Either<RoomError, List<Room>>

typealias RoomResult = Either<RoomError, Room>

class RoomService (
    private val roomRepository: RoomRepository,
    private val transactionInterface: TransactionInterface,
) {
    fun getAllRooms(): RoomListResult {
        return transactionInterface.useTransaction {
            val rooms = roomRepository.getAllRooms()
            return@useTransaction success(rooms)
        }
    }

    fun getRoomById(id: String?): RoomResult {
        return transactionInterface.useTransaction {
            if (id == null || id.toIntOrNull() == null) {
                return@useTransaction failure(RoomError.InvalidRoomId)
            }
            val room = roomRepository.getRoomById(id.toInt()) ?: return@useTransaction failure(RoomError.RoomNotFound)
            return@useTransaction success(room)
        }
    }

    fun createRoom(capacity: Int, name: String, type: String): RoomResult {
        return transactionInterface.useTransaction {
            if (capacity <= 0) {
                return@useTransaction failure(RoomError.InvalidRoomCapacity)
            }
            if (name.isBlank()) {
                return@useTransaction failure(RoomError.InvalidRoomData)
            }
            if (roomRepository.getAllRooms().any { it.name == name }) {
                return@useTransaction failure(RoomError.RoomAlreadyExists)
            }
            if (type.isBlank()) {
                return@useTransaction failure(RoomError.InvalidRoomData)
            }
            val room = roomRepository.createRoom(capacity, name) ?: return@useTransaction failure(RoomError.RoomNotFound)
            when (type) {
                "class" -> roomRepository.createClassRoom(room)
                "office" -> roomRepository.createOfficeRoom(room)
                "study" -> roomRepository.createStudyRoom(room)
                else -> return@useTransaction failure(RoomError.InvalidRoomData)
            }
            return@useTransaction success(room)
        }
    }
}