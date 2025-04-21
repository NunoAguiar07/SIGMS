package isel.leic.group25.services

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import isel.leic.group25.services.errors.SubjectError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success

typealias SubjectListResult = Either<SubjectError, List<Subject>>

typealias SubjectResult = Either<SubjectError, Subject>

typealias SubjectCreationResult = Either<SubjectError, Boolean>

class SubjectService(
    private val subjectRepository: SubjectRepository,
    private val transactionInterface: TransactionInterface,
) {
    fun getAllSubjects(limit:String?, offset:String?): SubjectListResult {
        val newLimit = limit?.toInt() ?: 20
        if (newLimit <= 0 || newLimit > 100) {
            return failure(SubjectError.InvalidSubjectLimit)
        }
        val newOffset = offset?.toInt() ?: 0
        if (newOffset < 0) {
            return failure(SubjectError.InvalidSubjectOffset)
        }
        return transactionInterface.useTransaction {
            val subjects = subjectRepository.getAllSubjects(newLimit, newOffset)
            return@useTransaction success(subjects)
        }
    }

    fun getSubjectById(id: String?): SubjectResult {
        return transactionInterface.useTransaction {
            if (id == null || id.toIntOrNull() == null) {
                return@useTransaction failure(SubjectError.InvalidSubjectId)
            }
            val subject = subjectRepository.findSubjectById(id.toInt()) ?: return@useTransaction failure(SubjectError.SubjectNotFound)
            return@useTransaction success(subject)
        }
    }

    fun createSubject(name: String): SubjectResult {
        return transactionInterface.useTransaction {
            val existingSubject = subjectRepository.findSubjectByName(name)
            if (existingSubject != null) {
                return@useTransaction failure(SubjectError.SubjectAlreadyExists)
            }
            val newSubject = subjectRepository.createSubject(name) ?: return@useTransaction failure(SubjectError.FailedToAddToDatabase)
            return@useTransaction success(newSubject)
        }
    }
}