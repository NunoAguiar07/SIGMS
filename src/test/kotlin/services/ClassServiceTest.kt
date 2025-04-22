package services

import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.timetables.ClassRepository
import repositories.DatabaseTestSetup


class ClassServiceTest {
    private val classRepository = ClassRepository(DatabaseTestSetup.database)
    private val transactionInterface = KTransaction(DatabaseTestSetup.database)

}