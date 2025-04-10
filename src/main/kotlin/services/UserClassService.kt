package isel.leic.group25.services

import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.users.UserRepository
import isel.leic.group25.services.errors.ClassError
import isel.leic.group25.services.errors.UserClassError

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success

typealias UserClassResult = Either<UserClassError, Any>

typealias UserClassListResult = Either<UserClassError, List<Class>>

class UserClassService(
    private val userRepository: UserRepository,
    private val classRepository: ClassRepository,
    private val transactionInterface: TransactionInterface
) {
    fun addStudentToClass(userId: Int, classId: Int): UserClassResult {
        return transactionInterface.useTransaction {
            val user = userRepository.findById(userId) ?: return@useTransaction failure(UserClassError.UserNotFound)
            val schoolClass = classRepository.findClassById(classId) ?: return@useTransaction failure(UserClassError.ClassNotFound)
            classRepository.addStudentToClass(user, schoolClass)
            return@useTransaction success(listOf(schoolClass))
        }
    }

    fun removeStudentFromClass(userId: Int, classId: Int): UserClassResult {
        return transactionInterface.useTransaction {
            val user = userRepository.findById(userId) ?: return@useTransaction failure(UserClassError.UserNotFound)
            val schoolClass = classRepository.findClassById(classId) ?: return@useTransaction failure(UserClassError.ClassNotFound)
            classRepository.deleteClass(schoolClass)
            return@useTransaction success(listOf(schoolClass))
        }
    }

    fun getScheduleByUserId(userId: Int, role: String): UserClassListResult {
        return transactionInterface.useTransaction {
            val classes = when (role) {
                "STUDENT" -> {
                    classRepository.findClassesByStudentId(userId)
                }
                "TEACHER" -> {
                    classRepository.findClassesByTeacherId(userId)
                }
                else -> {
                    return@useTransaction failure(UserClassError.InvalidRole)
                }
            }
            return@useTransaction success(classes)
        }
    }
}