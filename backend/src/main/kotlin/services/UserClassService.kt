package isel.leic.group25.services

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.db.repositories.interfaces.IsolationLevel
import isel.leic.group25.db.repositories.interfaces.Transactionable
import isel.leic.group25.services.errors.UserClassError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import java.sql.SQLException

typealias UserClassCreated = Either<UserClassError, Boolean>

typealias UserClassDeleted = Either<UserClassError, Boolean>

typealias UserLectureWithTeacherListResult = Either<UserClassError, List<Pair<Lecture, List<Teacher>>>>

typealias UserClassListResult = Either<UserClassError, List<Class>>

class UserClassService(
    private val repositories: Repositories,
    private val transactionable: Transactionable
) {
    private inline fun <T> runCatching(block: () -> Either<UserClassError, T>): Either<UserClassError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(UserClassError.ConnectionDbError(e.message))
        }
    }

    fun getUserClasses(userId: Int, role: Role, limit: Int, offset: Int): UserClassListResult {
        return runCatching {
            transactionable.useTransaction {
                val user = repositories.from({userRepository}){ findById(userId) }
                    ?: return@useTransaction failure(UserClassError.UserNotFound)

                return@useTransaction when (role) {
                    Role.STUDENT -> {
                        success(repositories.from({classRepository}){
                            findClassesByStudentId(user.id, limit, offset)
                        })
                    }
                    Role.TEACHER -> {
                        success(repositories.from({classRepository}){
                            findClassesByTeacherId(user.id, limit, offset)
                        })
                    }
                    else -> failure(UserClassError.InvalidRole)
                }
            }
        }
    }

    fun addUserToClass(userId: Int, classId: Int, role: Role): UserClassCreated {
        return runCatching {
            transactionable.useTransaction {
                val schoolClass = repositories.from({classRepository}){findClassById(classId)}
                    ?: return@useTransaction failure(UserClassError.ClassNotFound)
                return@useTransaction when (role) {
                    Role.STUDENT -> {
                        enrollStudent(userId, schoolClass)
                    }
                    Role.TEACHER -> {
                        assignTeacher(userId, schoolClass)
                    }
                    else -> return@useTransaction failure(UserClassError.InvalidRole)
                }
            }
        }
    }

    fun removeUserFromClass(userId: Int, classId: Int, role:Role): UserClassDeleted {
        return runCatching {
            transactionable.useTransaction(IsolationLevel.SERIALIZABLE) {
                val user = repositories.from({userRepository}){
                    findById(userId)
                } ?: return@useTransaction failure(UserClassError.UserNotFound)
                val schoolClass = repositories.from({classRepository}){findClassById(classId)}
                    ?: return@useTransaction failure(UserClassError.ClassNotFound)
                return@useTransaction when (role) {
                    Role.STUDENT -> {
                        withdrawStudent(user, schoolClass)
                    }
                    Role.TEACHER -> {
                        unassignTeacher(user, schoolClass)
                    }
                    else -> failure(UserClassError.InvalidRole)
                }
            }
        }
    }

    fun getScheduleByUserId(userId: Int, role: Role, limit:Int, offset:Int): UserLectureWithTeacherListResult {
        return runCatching {
            transactionable.useTransaction {
                val user = repositories.from({userRepository}){
                    findById(userId)
                } ?: return@useTransaction failure(UserClassError.UserNotFound)
                return@useTransaction when (role) {
                    Role.STUDENT -> {
                        studentSchedule(user, limit, offset)
                    }
                    Role.TEACHER -> {
                        teacherSchedule(user, limit, offset)
                    }
                    else -> failure(UserClassError.InvalidRole)
                }
            }
        }
    }

    fun getAllTeachersByUniversityId(universityId: Int, limit: Int, offset: Int): Either<UserClassError, List<Teacher>> {
        return runCatching {
            transactionable.useTransaction {
                val teachers = repositories.from({teacherRepository}) {
                    universityTeachers(universityId)
                }
                if (teachers.isEmpty()) {
                    return@useTransaction success(emptyList())
                }
                return@useTransaction success(teachers.sortedBy { it.user.username }.take(limit).drop(offset))
            }
        }
    }


    private fun enrollStudent(userId: Int, schoolClass: Class): Either<UserClassError, Boolean>{
        val student = repositories.from({studentRepository}){findStudentById(userId)}
            ?: return failure(UserClassError.UserNotFound)
        if (repositories.from({classRepository}){
                checkStudentInClass(student.user.id, schoolClass.id)
            }) {
            return failure(UserClassError.UserAlreadyInClass)
        }
        val linked = repositories.from({classRepository}){
            addStudentToClass(student, schoolClass)
        }
        if (!linked) {
            return failure(UserClassError.FailedToLinkUserToClass)
        }
        return success(true)
    }

    private fun assignTeacher(userId: Int, schoolClass: Class) : Either<UserClassError, Boolean>{
        val teacher = repositories.from({teacherRepository}){findTeacherById(userId)}
            ?: return failure(UserClassError.UserNotFound)
        if (repositories.from({classRepository}){
                checkTeacherInClass(teacher.user.id, schoolClass.id)
            }) {
            return failure(UserClassError.UserAlreadyInClass)
        }
        val linked = repositories.from({classRepository}){
            addTeacherToClass(teacher, schoolClass)
        }
        if (!linked) {
            return failure(UserClassError.FailedToLinkUserToClass)
        }
        return success(true)
    }

    private fun withdrawStudent(user: User, schoolClass: Class) : Either<UserClassError, Boolean> {
        if (!repositories.from({classRepository}){
                checkStudentInClass(user.id, schoolClass.id)
            }) {
            return failure(UserClassError.UserNotInClass)
        }
        val linked = repositories.from({classRepository}){
            removeStudentFromClass(user, schoolClass)
        }
        if (!linked) {
            return failure(UserClassError.FailedToUnlinkUserFromClass)
        }
        return success(true)
    }

    private fun unassignTeacher(user: User, schoolClass: Class) : Either<UserClassError, Boolean>{
        if (!repositories.from({classRepository}){
                checkTeacherInClass(user.id, schoolClass.id)
            }) {
            return failure(UserClassError.UserNotInClass)
        }
        val linked = repositories.from({classRepository}){
            removeTeacherFromClass(user, schoolClass)
        }
        if (!linked) {
            return failure(UserClassError.FailedToUnlinkUserFromClass)
        }
        return success(true)
    }

    private fun studentSchedule(user: User, limit: Int, offset: Int): Either<UserClassError, List<Pair<Lecture, List<Teacher>>>> {
        val classes = repositories.from({ classRepository }) {
            findClassesByStudentId(user.id)
        }
        if (classes.isEmpty()) {
            return success(emptyList())
        }

        val lectureTeacherPairs = classes.flatMap { classEntity ->
            val teacher = repositories.from({ teacherRepository }) {
                findTeachersByClassId(classEntity.id)
            }

            repositories.from({ lectureRepository }) {
                getLecturesByClass(classEntity.id, limit, offset)
            }.map { lecture ->
                lecture to teacher
            }
        }.sortedBy { it.first.weekDay }
        println(lectureTeacherPairs)
        return success(lectureTeacherPairs)
    }

    private fun teacherSchedule(user: User, limit: Int, offset: Int): Either<UserClassError, List<Pair<Lecture, List<Teacher>>>> {
        val classes = repositories.from({ classRepository }) {
            findClassesByTeacherId(user.id)
        }

        if (classes.isEmpty()) {
            return success(emptyList())
        }

        val lectureTeacherPairs = classes.flatMap { classEntity ->
            val teachers = repositories.from({ teacherRepository }) {
                findTeachersByClassId(classEntity.id)
            }

            repositories.from({ lectureRepository }) {
                getLecturesByClass(classEntity.id, limit, offset)
            }.map { lecture ->
                lecture to teachers
            }
        }.sortedBy { it.first.weekDay }

        return success(lectureTeacherPairs)
    }
}

