package isel.leic.group25.services

import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.services.errors.ClassError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.timetables.SubjectRepository

typealias ClassListResult = Either<ClassError, List<Class>>

typealias ClassResult = Either<ClassError, Class>

class ClassService(private val classRepository: ClassRepository,
                   private val subjectRepository: SubjectRepository,
                   private val transactionInterface: TransactionInterface,
) {
    fun getAllClassesFromSubject(subjectId: String?): ClassListResult {
        return transactionInterface.useTransaction {
            if (subjectId == null || subjectId.toIntOrNull() == null) {
                return@useTransaction failure(ClassError.InvalidSubjectId)
            }
            val subject = subjectRepository.findSubjectById(subjectId.toInt())
                ?: return@useTransaction failure(ClassError.SubjectNotFound)
            val classes = classRepository.findClassesBySubject(subject)
            return@useTransaction success(classes)
        }
    }

    fun getClassById(id: String?): ClassResult {
        return transactionInterface.useTransaction {
            if (id == null || id.toIntOrNull() == null) {
                return@useTransaction failure(ClassError.InvalidClassData)
            }
            val schoolClass = classRepository.findClassById(id.toInt())
                ?: return@useTransaction failure(ClassError.ClassNotFound)
            return@useTransaction success(schoolClass)
        }
    }

    fun createClass(name: String, subjectId: String?): ClassResult {
        return transactionInterface.useTransaction {
            if (name.isBlank()) {
                return@useTransaction failure(ClassError.InvalidClassData)
            }
            if (subjectId == null || subjectId.toIntOrNull() == null) {
                return@useTransaction failure(ClassError.InvalidSubjectId)
            }
            classRepository.findClassByName(name) ?: return@useTransaction failure(ClassError.ClassAlreadyExists)
            val existingSubject = subjectRepository.findSubjectById(subjectId.toInt())
                ?: return@useTransaction failure(ClassError.SubjectNotFound)
            val newClass = Class {
                this.name = name
                this.subject = existingSubject
            }
            classRepository.addClass(newClass)
            return@useTransaction success(newClass)
        }
    }
}