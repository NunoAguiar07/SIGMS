package isel.leic.group25.services

import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.services.errors.UserClassError
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.repositories.timetables.interfaces.ClassRepositoryInterface
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.StudentRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.TeacherRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.UserRepositoryInterface
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success

typealias UserClassCreated = Either<UserClassError, Boolean>

typealias UserClassDeleted = Either<UserClassError, Boolean>

typealias UserLectureListResult = Either<UserClassError, List<Lecture>>

class UserClassService(
    private val userRepository: UserRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val teacherRepository: TeacherRepositoryInterface,
    private val classRepository: ClassRepositoryInterface,
    private val lectureRepository: LectureRepositoryInterface,
    private val transactionInterface: TransactionInterface
) {
    fun addUserToClass(userId: Int?, classId: String?, role: String?): UserClassCreated {
        if(userId == null) {
            return failure(UserClassError.InvalidUserId)
        }
        if (classId == null || classId.toIntOrNull() == null) {
            return failure(UserClassError.InvalidClassId)
        }
        if (role.isNullOrBlank()) {
            return failure(UserClassError.InvalidRole)
        }
        return transactionInterface.useTransaction {
            val schoolClass = classRepository.findClassById(classId.toInt()) ?: return@useTransaction failure(UserClassError.ClassNotFound)
            when (role) {
                "STUDENT" -> {
                    val student = studentRepository.findStudentById(userId) ?: return@useTransaction failure(UserClassError.UserNotFound)
                    if (classRepository.checkStudentInClass(student.user.id, schoolClass.id)) {
                        return@useTransaction failure(UserClassError.UserAlreadyInClass)
                    }
                    val liked = classRepository.addStudentToClass(student, schoolClass)
                    if (!liked) {
                        return@useTransaction failure(UserClassError.FailedToLinkUserToClass)
                    }
                    return@useTransaction success(true)
                }
                "TEACHER" -> {
                    val teacher = teacherRepository.findTeacherById(userId) ?: return@useTransaction failure(UserClassError.UserNotFound)
                    if (classRepository.checkTeacherInClass(teacher.user.id, schoolClass.id)) {
                        return@useTransaction failure(UserClassError.UserAlreadyInClass)
                    }
                    val liked = classRepository.addTeacherToClass(teacher, schoolClass)
                    if (!liked) {
                        return@useTransaction failure(UserClassError.FailedToLinkUserToClass)
                    }
                    return@useTransaction success(true)
                }
                else -> return@useTransaction failure(UserClassError.InvalidRole)
            }
        }
    }

    fun removeUserFromClass(userId: Int?, classId: String?, role:String?): UserClassDeleted {
        if(userId == null) {
            return failure(UserClassError.InvalidUserId)
        }
        if (classId == null || classId.toIntOrNull() == null) {
            return failure(UserClassError.InvalidClassId)
        }
        if (role.isNullOrBlank()) {
            return failure(UserClassError.InvalidRole)
        }
        return transactionInterface.useTransaction {
            val user = userRepository.findById(userId) ?: return@useTransaction failure(UserClassError.UserNotFound)
            val schoolClass = classRepository.findClassById(classId.toInt()) ?: return@useTransaction failure(UserClassError.ClassNotFound)
            when (role) {
                "STUDENT" -> {
                    if (!classRepository.checkStudentInClass(user.id, schoolClass.id)) {
                        return@useTransaction failure(UserClassError.UserNotInClass)
                    }
                    val liked = classRepository.removeStudentFromClass(user, schoolClass)
                    if (!liked) {
                        return@useTransaction failure(UserClassError.FailedToUnlinkUserFromClass)
                    }
                    return@useTransaction success(true)
                }
                "TEACHER" -> {
                    if (!classRepository.checkTeacherInClass(user.id, schoolClass.id)) {
                        return@useTransaction failure(UserClassError.UserNotInClass)
                    }
                    val liked = classRepository.removeTeacherFromClass(user, schoolClass)
                    if (!liked) {
                        return@useTransaction failure(UserClassError.FailedToUnlinkUserFromClass)
                    }
                    return@useTransaction success(true)
                }
                else -> return@useTransaction failure(UserClassError.InvalidRole)
            }
        }
    }

    fun getScheduleByUserId(userId: Int?, role: String?): UserLectureListResult {
        if(userId == null) {
            return failure(UserClassError.InvalidUserId)
        }
        if (role.isNullOrBlank()) {
            return failure(UserClassError.InvalidRole)
        }
        return transactionInterface.useTransaction {
            val user = userRepository.findById(userId) ?: return@useTransaction failure(UserClassError.UserNotFound)
            when (role) {
                "STUDENT" -> {
                    val classes = classRepository.findClassesByStudentId(user.id)
                    if(classes.isEmpty()) {
                        return@useTransaction success(emptyList<Lecture>())
                    }
                    val lectures = lectureRepository.getLecturesByClass(classes[0].id, 50, 0)
                    if(lectures.isEmpty()) {
                        return@useTransaction success(emptyList<Lecture>())
                    }
                    return@useTransaction success(lectures)
                }
                "TEACHER" -> {
                    val classes = classRepository.findClassesByTeacherId(user.id)
                    if(classes.isEmpty()) {
                        return@useTransaction success(emptyList<Lecture>())
                    }
                    val lectures = lectureRepository.getLecturesByClass(classes[0].id, 100, 0)
                    if(lectures.isEmpty()) {
                        return@useTransaction success(emptyList<Lecture>())
                    }
                    return@useTransaction success(lectures)
                }
                else -> return@useTransaction failure(UserClassError.InvalidRole)
            }
        }
    }
}

