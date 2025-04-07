package isel.leic.group25.services

import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.services.errors.ClassError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import org.ktorm.database.Database
import isel.leic.group25.db.entities.timetables.Class

typealias ClassResult = Either<ClassError, List<Class>>

class ClassService(private val repository: ClassRepository,
                   private val database: Database
) {
    fun getScheduleByUserId(userId: Int, role: String): ClassResult {
        return database.useTransaction {
            val classes = when (role) {
                "STUDENT" -> {
                    repository.findClassesByStudentId(userId)
                }
                "TEACHER" -> {
                    repository.findClassesByTeacherId(userId)
                }
                else -> {
                    return@useTransaction failure(ClassError.InvalidRole)
                }
            }
            return@useTransaction success(classes)
        }
    }
}