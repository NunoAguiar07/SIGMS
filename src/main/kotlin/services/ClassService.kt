package isel.leic.group25.services

import isel.leic.group25.services.errors.ClassError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.timetables.interfaces.ClassRepositoryInterface
import isel.leic.group25.db.repositories.timetables.interfaces.SubjectRepositoryInterface
import java.sql.SQLException

typealias ClassListResult = Either<ClassError, List<Class>>

typealias ClassResult = Either<ClassError, Class>

typealias DeleteClassResult = Either<ClassError, Boolean>

class ClassService(private val classRepository: ClassRepositoryInterface,
                   private val subjectRepository: SubjectRepositoryInterface,
                   private val transactionInterface: TransactionInterface,
) {
    private inline fun <T> runCatching(block: () -> Either<ClassError, T>): Either<ClassError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(ClassError.ConnectionDbError(e.message))
        }
    }

    fun getAllClassesFromSubject(subjectId: Int, limit:Int, offset:Int): ClassListResult {
        return runCatching {
            transactionInterface.useTransaction {
                val subject = subjectRepository.findSubjectById(subjectId)
                    ?: return@useTransaction failure(ClassError.SubjectNotFound)
                val classes = classRepository.findClassesBySubject(subject, limit, offset)
                return@useTransaction success(classes)
            }
        }
    }

    fun getClassById(id: Int): ClassResult {
        return runCatching {
            transactionInterface.useTransaction {
                val schoolClass = classRepository.findClassById(id)
                    ?: return@useTransaction failure(ClassError.ClassNotFound)
                return@useTransaction success(schoolClass)
            }
        }
    }

    fun createClass(name: String, subjectId: Int): ClassResult {
        return runCatching {
            transactionInterface.useTransaction {
                val existingClass = classRepository.findClassByName(name)
                if(existingClass != null) {
                    return@useTransaction failure(ClassError.ClassAlreadyExists)
                }
                val existingSubject = subjectRepository.findSubjectById(subjectId)
                    ?: return@useTransaction failure(ClassError.SubjectNotFound)
                val newClass = classRepository.addClass(name, existingSubject)
                return@useTransaction success(newClass)
            }
        }
    }
}