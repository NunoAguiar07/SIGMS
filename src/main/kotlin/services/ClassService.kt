package isel.leic.group25.services

import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.services.errors.ClassError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.repositories.interfaces.TransactionInterface

typealias ClassResult = Either<ClassError, List<Class>>

class ClassService(private val repository: ClassRepository,
                   private val transactionInterface: TransactionInterface,
) {

}