package isel.leic.group25.services

import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.db.repositories.interfaces.Transactionable
import isel.leic.group25.services.errors.TeacherRoomError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import java.sql.SQLException

typealias AddTeacherRoomResult = Either<TeacherRoomError, Teacher>

typealias RemoveTeacherRoomResult = Either<TeacherRoomError, Teacher>

class TeacherRoomService (
    private val repositories: Repositories,
    private val transactionable: Transactionable
) {
    private inline fun <T> runCatching(block: () -> Either<TeacherRoomError, T>): Either<TeacherRoomError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(TeacherRoomError.ConnectionDbError(e.message))
        }
    }

    fun addTeacherToOffice(teacherId: Int, officeId: Int) : AddTeacherRoomResult{
        return runCatching {
            transactionable.useTransaction {
                val teacher = repositories.from({teacherRepository}) {
                    findTeacherById(teacherId)
                } ?: return@useTransaction failure(TeacherRoomError.TeacherNotFound)
                val room = repositories.from({roomRepository}) {
                    getOfficeRoomById(officeId)
                } ?: return@useTransaction failure(TeacherRoomError.RoomNotFound)
                val newTeacher = repositories.from({roomRepository}) {
                    addTeacherToOffice(teacher, room)
                }
                return@useTransaction success(newTeacher)
            }
        }
    }

    fun removeTeacherFromRoom(teacherId: Int, officeId: Int) : RemoveTeacherRoomResult {
        return runCatching {
            transactionable.useTransaction {
                val teacher = repositories.from({teacherRepository}) {
                    findTeacherById(teacherId)
                } ?: return@useTransaction failure(TeacherRoomError.TeacherNotFound)
                val room = repositories.from({roomRepository}) {
                    getOfficeRoomById(officeId)
                } ?: return@useTransaction failure(TeacherRoomError.RoomNotFound)

                val newTeacher = repositories.from({roomRepository}) {
                    removeTeacherFromOffice(teacher, room)
                }
                return@useTransaction success(newTeacher)
            }
        }
    }
}