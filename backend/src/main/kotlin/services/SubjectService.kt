package isel.leic.group25.services

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.db.repositories.interfaces.Transactionable
import isel.leic.group25.services.errors.SubjectError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import java.sql.SQLException

typealias SubjectListResult = Either<SubjectError, List<Subject>>

typealias SubjectResult = Either<SubjectError, Subject>

typealias DeleteSubjectResult = Either<SubjectError, Boolean>

class SubjectService(
    private val repositories: Repositories,
    private val transactionable: Transactionable,
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
            transactionable.useTransaction {
                val subjects = repositories.from({subjectRepository}) {getAllSubjects(limit, offset)}
                return@useTransaction success(subjects)
            }
        }
    }

    fun getAllSubjectsByUniversity(universityId: Int, limit: Int, offset: Int): SubjectListResult {
        return runCatching {
            transactionable.useTransaction {
                val university = repositories.from({universityRepository}) {getUniversityById(universityId)}
                    ?: return@useTransaction failure(SubjectError.UniversityNotFound)
                val subjects = repositories.from({subjectRepository}) {
                    getAllSubjectsByUniversityId(university.id, limit, offset)
                }
                return@useTransaction success(subjects)
            }
        }
    }

    fun getSubjectsByNameAndUniversityId(
        universityId: Int,
        subjectPartialName: String,
        limit: Int,
        offset: Int
    ): SubjectListResult {
        return runCatching {
            transactionable.useTransaction {
                val university = repositories.from({universityRepository}) {getUniversityById(universityId)}
                    ?: return@useTransaction failure(SubjectError.UniversityNotFound)
                val subjects = repositories.from({subjectRepository}) {
                    getSubjectsByNameAndUniversityId(university.id, subjectPartialName, limit, offset)
                }
                return@useTransaction success(subjects)
            }
        }
    }

    fun getSubjectById(id: Int): SubjectResult {
        return runCatching {
            transactionable.useTransaction {
                val subject = repositories.from({subjectRepository}) {findSubjectById(id)}
                    ?: return@useTransaction failure(SubjectError.SubjectNotFound)
                return@useTransaction success(subject)
            }
        }
    }

    fun createSubject(name: String, universityId: Int): SubjectResult {
        return runCatching {
            transactionable.useTransaction {
                val university = repositories.from({universityRepository}) {
                    getUniversityById(universityId)
                } ?: return@useTransaction failure(SubjectError.UniversityNotFound)
                val existingSubject = repositories.from({subjectRepository}) {
                    findSubjectByName(name)
                }
                if (existingSubject != null) {
                    return@useTransaction failure(SubjectError.SubjectAlreadyExists)
                }
                val newSubject = repositories.from({subjectRepository}) {
                    createSubject(name, university)
                }
                return@useTransaction success(newSubject)
            }
        }
    }

    fun deleteSubject(id: Int): DeleteSubjectResult {
        return runCatching {
            transactionable.useTransaction {
                val subject = repositories.from({subjectRepository}) {findSubjectById(id)}
                    ?: return@useTransaction failure(SubjectError.SubjectNotFound)
                val deleted = repositories.from({subjectRepository}) {
                    deleteSubject(subject.id)
                }
                if (!deleted) {
                    return@useTransaction failure(SubjectError.SubjectNotFound)
                }
                return@useTransaction success(true)
            }
        }
    }
}