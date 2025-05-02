package isel.leic.group25.services

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.timetables.interfaces.SubjectRepositoryInterface
import isel.leic.group25.services.errors.SubjectError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success

typealias SubjectListResult = Either<SubjectError, List<Subject>>

typealias SubjectResult = Either<SubjectError, Subject>

class SubjectService(
    private val subjectRepository: SubjectRepositoryInterface,
    private val transactionInterface: TransactionInterface,
) {
    fun getAllSubjects(limit:Int, offset:Int): SubjectListResult {
        return transactionInterface.useTransaction {
            val subjects = subjectRepository.getAllSubjects(limit, offset)
            return@useTransaction success(subjects)
        }
    }

    fun getSubjectById(id: Int): SubjectResult {
        return transactionInterface.useTransaction {
            val subject = subjectRepository.findSubjectById(id) ?: return@useTransaction failure(SubjectError.SubjectNotFound)
            return@useTransaction success(subject)
        }
    }

    fun createSubject(name: String): SubjectResult {
        return transactionInterface.useTransaction {
            val existingSubject = subjectRepository.findSubjectByName(name)
            if (existingSubject != null) {
                return@useTransaction failure(SubjectError.SubjectAlreadyExists)
            }
            val newSubject = subjectRepository.createSubject(name)
            return@useTransaction success(newSubject)
        }
    }

    fun deleteSubject(id: Int): SubjectResult {
        return transactionInterface.useTransaction {
            val subject = subjectRepository.findSubjectById(id) ?: return@useTransaction failure(SubjectError.SubjectNotFound)
            val deleted = subjectRepository.deleteSubject(subject.id)
            if (!deleted) {
                return@useTransaction failure(SubjectError.SubjectNotFound)
            }
            return@useTransaction success(subject)
        }
    }
}