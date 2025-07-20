package isel.leic.group25.services

import isel.leic.group25.services.errors.ClassError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.db.repositories.interfaces.IsolationLevel
import isel.leic.group25.db.repositories.interfaces.Transactionable
import java.sql.SQLException

typealias ClassListResult = Either<ClassError, List<Class>>

typealias ClassResult = Either<ClassError, Class>

typealias DeleteClassResult = Either<ClassError, Boolean>

class ClassService(private val repositories: Repositories,
                   private val transactionable: Transactionable,
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
            transactionable.useTransaction {
                val subject = repositories.from({subjectRepository}){findSubjectById(subjectId)}
                    ?: return@useTransaction failure(ClassError.SubjectNotFound)
                val classes = repositories.from({classRepository}){
                    findClassesBySubject(subject, limit, offset)
                }
                return@useTransaction success(classes)
            }
        }
    }

    fun getClassById(id: Int): ClassResult {
        return runCatching {
            transactionable.useTransaction {
                val schoolClass = repositories.from({classRepository}){findClassById(id)}
                    ?: return@useTransaction failure(ClassError.ClassNotFound)
                return@useTransaction success(schoolClass)
            }
        }
    }

    fun createClass(name: String, subjectId: Int): ClassResult {
        return runCatching {
            transactionable.useTransaction {
                val subject = repositories.from({subjectRepository}) { findSubjectById(subjectId) }
                    ?: return@useTransaction failure(ClassError.SubjectNotFound)

                val existingClassInSubject = repositories.from({classRepository}) {
                    findClassInSubjectByName(subject, name)
                }
                if (existingClassInSubject != null) {
                    return@useTransaction failure(ClassError.ClassAlreadyExists)
                }

                val newClass = repositories.from({classRepository}) {
                    addClass(name, subject)
                }
                return@useTransaction success(newClass)
            }
        }
    }

    fun deleteClass(id: Int): DeleteClassResult {
        return runCatching {
            transactionable.useTransaction(IsolationLevel.SERIALIZABLE) {
                val schoolClass = repositories.from({classRepository}){findClassById(id)}
                    ?: return@useTransaction failure(ClassError.ClassNotFound)

                val deleted = repositories.from({classRepository}){deleteClass(schoolClass)}
                if (deleted) {
                    return@useTransaction success(true)
                } else {
                    return@useTransaction failure(ClassError.ClassDeletionFailed)
                }
            }
        }
    }

}