package isel.leic.group25.services

import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.services.errors.ClassError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.repositories.interfaces.TransactionInterface
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import kotlinx.datetime.Instant

typealias ClassListResult = Either<ClassError, List<Class>>

typealias ClassResult = Either<ClassError, Class>

class ClassService(private val classRepository: ClassRepository,
                   private val subjectRepository: SubjectRepository,
                   private val transactionInterface: TransactionInterface,
) {
    fun getAllClasses(): ClassListResult {
        return transactionInterface.useTransaction {
            val classes = classRepository.findAllClasses()
            if (classes.isEmpty()) {
                return@useTransaction failure(ClassError.ClassNotFound)
            }
            return@useTransaction success(classes)
        }
    }

    fun createClass(name: String, subjectId: Int, classType: String, startTime: String, endTime: String): ClassResult {
        return transactionInterface.useTransaction {
            classRepository.findClassByName(name) ?: return@useTransaction failure(ClassError.ClassAlreadyExists)
            val existingSubject = subjectRepository.findSubjectById(subjectId)
                ?: return@useTransaction failure(ClassError.SubjectNotFound)
            val newClass = Class {
                this.name = name
                this.subject = existingSubject
                this.type = ClassType.valueOf(classType)
                this.startTime = Instant.parse(startTime)
                this.endTime = Instant.parse(endTime)
            }
            classRepository.addClass(newClass)
            return@useTransaction success(newClass)
        }
    }
}