package isel.leic.group25.services

import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.services.errors.UserClassError
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.Role
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
    fun addUserToClass(userId: Int, classId: Int, role: Role): UserClassCreated {
        return transactionInterface.useTransaction {
            val schoolClass = classRepository.findClassById(classId) ?: return@useTransaction failure(UserClassError.ClassNotFound)
            when (role) {
                Role.STUDENT -> {
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
                Role.TEACHER -> {
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

    fun removeUserFromClass(userId: Int, classId: Int, role:Role): UserClassDeleted {
        return transactionInterface.useTransaction {
            val user = userRepository.findById(userId) ?: return@useTransaction failure(UserClassError.UserNotFound)
            val schoolClass = classRepository.findClassById(classId) ?: return@useTransaction failure(UserClassError.ClassNotFound)
            when (role) {
                Role.STUDENT -> {
                    if (!classRepository.checkStudentInClass(user.id, schoolClass.id)) {
                        return@useTransaction failure(UserClassError.UserNotInClass)
                    }
                    val liked = classRepository.removeStudentFromClass(user, schoolClass)
                    if (!liked) {
                        return@useTransaction failure(UserClassError.FailedToUnlinkUserFromClass)
                    }
                    return@useTransaction success(true)
                }
                Role.TEACHER -> {
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

    fun getScheduleByUserId(userId: Int, role: Role): UserLectureListResult {
        return transactionInterface.useTransaction {
            val user = userRepository.findById(userId) ?: return@useTransaction failure(UserClassError.UserNotFound)
            when (role) {
                Role.STUDENT -> {
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
                Role.TEACHER -> {
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

