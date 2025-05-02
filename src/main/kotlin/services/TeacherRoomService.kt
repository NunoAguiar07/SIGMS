package isel.leic.group25.services

import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.rooms.interfaces.RoomRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.TeacherRepositoryInterface
import isel.leic.group25.services.errors.TeacherRoomError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success

typealias AddTeacherRoomResult = Either<TeacherRoomError, Teacher>

typealias RemoveTeacherRoomResult = Either<TeacherRoomError, Teacher>

class TeacherRoomService (
    private val teacherRepository: TeacherRepositoryInterface,
    private val roomRepository: RoomRepositoryInterface,
    private val transactionInterface: TransactionInterface
) {
    fun addTeacherToOffice(teacherId: Int, officeId: Int) : AddTeacherRoomResult{
        return transactionInterface.useTransaction {
            val teacher = teacherRepository.findTeacherById(teacherId)
                ?: return@useTransaction failure(TeacherRoomError.TeacherNotFound)
            val room = roomRepository.getOfficeRoomById(officeId)
                ?: return@useTransaction failure(TeacherRoomError.RoomNotFound)
            val newTeacher = roomRepository.addTeacherToOffice(teacher, room)
            return@useTransaction success(newTeacher)
        }
    }

    fun removeTeacherFromRoom(teacherId: Int, officeId: Int) : RemoveTeacherRoomResult {
        return transactionInterface.useTransaction {
            val teacher = teacherRepository.findTeacherById(teacherId)
                ?: return@useTransaction failure(TeacherRoomError.TeacherNotFound)
            val room = roomRepository.getOfficeRoomById(officeId)
                ?: return@useTransaction failure(TeacherRoomError.RoomNotFound)
            val newTeacher = roomRepository.removeTeacherFromOffice(teacher, room)
            return@useTransaction success(newTeacher)
        }
    }
}