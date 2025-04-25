package isel.leic.group25.services

import isel.leic.group25.services.errors.ClassError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.timetables.interfaces.ClassRepositoryInterface
import isel.leic.group25.db.repositories.timetables.interfaces.SubjectRepositoryInterface

typealias ClassListResult = Either<ClassError, List<Class>>

typealias ClassResult = Either<ClassError, Class>

class ClassService(private val classRepository: ClassRepositoryInterface,
                   private val subjectRepository: SubjectRepositoryInterface,
                   private val transactionInterface: TransactionInterface,
) {
    fun getAllClassesFromSubject(subjectId: String?, limit:String?, offset:String?): ClassListResult {
        return transactionInterface.useTransaction {
            val newLimit = limit?.toInt() ?: 20
            if (newLimit <= 0 || newLimit > 100) {
                return@useTransaction failure(ClassError.InvalidClassLimit)
            }
            val newOffset = offset?.toInt() ?: 0
            if (newOffset < 0) {
                return@useTransaction failure(ClassError.InvalidClassOffset)
            }
            if (subjectId == null || subjectId.toIntOrNull() == null) {
                return@useTransaction failure(ClassError.InvalidSubjectId)
            }
            val subject = subjectRepository.findSubjectById(subjectId.toInt())
                ?: return@useTransaction failure(ClassError.SubjectNotFound)
            val classes = classRepository.findClassesBySubject(subject, newLimit, newOffset)
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
            val existingClass = classRepository.findClassByName(name)
            if(existingClass != null && existingClass.subject.id == subjectId.toInt()) {
                return@useTransaction failure(ClassError.ClassAlreadyExists)
            }
            val existingSubject = subjectRepository.findSubjectById(subjectId.toInt())
                ?: return@useTransaction failure(ClassError.SubjectNotFound)
            val newClass = Class {
                this.name = name
                this.subject = existingSubject
            }
            classRepository.addClass(newClass.name, newClass.subject)
            return@useTransaction success(newClass)
        }
    }
}