package isel.leic.group25.services

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.types.RoomType
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.db.repositories.interfaces.IsolationLevel
import isel.leic.group25.db.repositories.interfaces.Transactionable
import isel.leic.group25.services.errors.RoomError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import java.sql.SQLException

typealias RoomListResult = Either<RoomError, List<Room>>

typealias RoomResult = Either<RoomError, Room>

typealias DeleteRoomResult = Either<RoomError, Boolean>

class RoomService (
    private val repositories: Repositories,
    private val transactionable: Transactionable,
) {
    private inline fun <T> runCatching(block: () -> Either<RoomError, T>): Either<RoomError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(RoomError.ConnectionDbError(e.message))
        }
    }

    fun getRoomsByNameByTypeAndUniversityId(
        universityId: Int,
        roomPartialName: String,
        roomType: RoomType?,
        limit: Int,
        offset: Int
    ): RoomListResult {
        return runCatching {
            transactionable.useTransaction {
                val university = repositories.from({universityRepository}) {
                    getUniversityById(universityId)
                } ?: return@useTransaction failure(RoomError.UniversityNotFound)
                if (roomType == null) {
                    val rooms = repositories.from({roomRepository}) {
                        getAllRoomsByNameAndUniversityId(university.id, roomPartialName, limit, offset)
                    }
                    return@useTransaction success(rooms)
                }
                val rooms = repositories.from({roomRepository}) {
                    getAllRoomsByNameByTypeAndUniversityId(university.id, roomPartialName, roomType, limit, offset)
                }
                return@useTransaction success(rooms)
            }
        }
    }

    fun getAllRoomsByUniversityIdAndType(
        universityId: Int,
        roomType: RoomType,
        limit: Int,
        offset: Int
    ): RoomListResult {
        return runCatching {
            transactionable.useTransaction {
                val university = repositories.from({universityRepository}) { getUniversityById(universityId) } ?: return@useTransaction failure(RoomError.UniversityNotFound)
                val rooms = repositories.from({roomRepository}) {
                    getAllRoomsByUniversityIdAndType(university.id, roomType, limit, offset)
                }
                return@useTransaction success(rooms)
            }
        }
    }

    fun getAllRooms(limit: Int, offset: Int): RoomListResult {
        return runCatching {
            transactionable.useTransaction {
                val rooms = repositories.from({roomRepository}){getAllRooms(limit, offset)}
                return@useTransaction success(rooms)
            }
        }
    }

    fun getUniversityListOfRoomsByIds(universityId: Int, roomIdList: List<Int>): RoomListResult {
        return runCatching {
            transactionable.useTransaction {
                val rooms = repositories.from({roomRepository}){
                    roomIdList.mapNotNull {
                        val room = getRoomById(it)
                        if(room?.university?.id == universityId){
                            room
                        } else {
                            null
                        }
                    }
                }
                return@useTransaction success(rooms)
            }
        }
    }

    fun getAllRoomsByUniversityId(universityId: Int, limit: Int, offset: Int): RoomListResult {
        return runCatching {
            transactionable.useTransaction {
                val university = repositories.from({universityRepository}){
                    getUniversityById(universityId)
                }
                ?: return@useTransaction failure(RoomError.UniversityNotFound)
                val rooms = repositories.from({roomRepository}){
                    getAllRoomsByUniversityId(university.id, limit, offset)
                }
                return@useTransaction success(rooms)
            }
        }
    }

    fun getRoomById(id: Int): RoomResult {
        return runCatching {
            transactionable.useTransaction {
                val room = repositories.from({roomRepository}){
                    getRoomById(id)
                } ?: return@useTransaction failure(RoomError.RoomNotFound)
                return@useTransaction success(room)
            }
        }
    }

    fun createRoom(capacity: Int, name: String, universityId: Int, type: RoomType): RoomResult {
        return runCatching {
            transactionable.useTransaction {
                val university = repositories.from({universityRepository}){
                    getUniversityById(universityId)
                }
                    ?: return@useTransaction failure(RoomError.UniversityNotFound)
                val room = repositories.from({roomRepository}){
                    createRoom(capacity, name, university)
                }
                when (type) {
                    RoomType.CLASS -> repositories.from({roomRepository}){
                        createClassRoom(room)
                    }
                    RoomType.OFFICE -> repositories.from({roomRepository}){
                        createOfficeRoom(room)
                    }
                    RoomType.STUDY -> repositories.from({roomRepository}){
                        createStudyRoom(room)
                    }
                }
                return@useTransaction success(room)
            }
        }
    }
    fun deleteRoom(id: Int): DeleteRoomResult {
        return runCatching {
            transactionable.useTransaction(IsolationLevel.SERIALIZABLE) {
                val room = repositories.from({roomRepository}){getRoomById(id)}
                    ?: return@useTransaction failure(RoomError.RoomNotFound)
                if (repositories.from({roomRepository}){deleteRoom(room.id)}) {
                    return@useTransaction success(true)
                }
                return@useTransaction failure(RoomError.ConnectionDbError("Failed to delete room with id $id"))
            }
        }
    }

    fun updateRoom(id: Int, name: String, capacity: Int): RoomResult {
        return runCatching {
            transactionable.useTransaction(IsolationLevel.SERIALIZABLE) {
                val room = repositories.from({roomRepository}){getRoomById(id)}
                    ?: return@useTransaction failure(RoomError.RoomNotFound)
                val updatedRoom = repositories.from({roomRepository}){updateRoom(room, name, capacity)}
                return@useTransaction success(updatedRoom)
            }
        }
    }


}