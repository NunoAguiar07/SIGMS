package isel.leic.group25.services

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.timetables.interfaces.SubjectRepositoryInterface
import isel.leic.group25.services.errors.SubjectError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import java.sql.SQLException

typealias SubjectListResult = Either<SubjectError, List<Subject>>

typealias SubjectResult = Either<SubjectError, Subject>

typealias DeleteSubjectResult = Either<SubjectError, Boolean>

class SubjectService(
    private val subjectRepository: SubjectRepositoryInterface,
    private val transactionInterface: TransactionInterface,
) {
    private inline fun <T> runCatching(block: () -> Either<SubjectError, T>): Either<SubjectError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(SubjectError.ConnectionDbError(e.message))
        }
    }

    fun getAllSubjects(limit:Int, offset:Int): SubjectListResult {
        return runCatching {
            transactionInterface.useTransaction {
                val subjects = subjectRepository.getAllSubjects(limit, offset)
                return@useTransaction success(subjects)
            }
        }
    }

    fun getSubjectById(id: Int): SubjectResult {
        return runCatching {
            transactionInterface.useTransaction {
                val subject = subjectRepository.findSubjectById(id) ?: return@useTransaction failure(SubjectError.SubjectNotFound)
                return@useTransaction success(subject)
            }
        }
    }

    fun createSubject(name: String): SubjectResult {
        return runCatching {
            transactionInterface.useTransaction {
                val existingSubject = subjectRepository.findSubjectByName(name)
                if (existingSubject != null) {
                    return@useTransaction failure(SubjectError.SubjectAlreadyExists)
                }
                val newSubject = subjectRepository.createSubject(name)
                return@useTransaction success(newSubject)
            }
        }
    }

    fun deleteSubject(id: Int): DeleteSubjectResult {
        return runCatching {
            transactionInterface.useTransaction {
                val subject = subjectRepository.findSubjectById(id) ?: return@useTransaction failure(SubjectError.SubjectNotFound)
                val deleted = subjectRepository.deleteSubject(subject.id)
                if (!deleted) {
                    return@useTransaction failure(SubjectError.SubjectNotFound)
                }
                return@useTransaction success(true)
            }
        }
    }
}