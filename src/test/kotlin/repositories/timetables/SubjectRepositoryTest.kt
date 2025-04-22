package repositories.timetables

import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import repositories.DatabaseTestSetup
import kotlin.test.AfterTest
import kotlin.test.Test

// check comments before changing tests

class SubjectRepositoryTest {
    private val kTransaction = KTransaction(DatabaseTestSetup.database)
    private val subjectRepository = SubjectRepository(DatabaseTestSetup.database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `Should create a new subject and find it by id`() {
        kTransaction.useTransaction {
            val newSubject = subjectRepository.createSubject("testSubject")
            val foundSubject = subjectRepository.findSubjectById(newSubject.id)
            assert(foundSubject != null)
            assert(foundSubject?.name == newSubject.name)
        }
    }

    @Test
    fun `Should create a new subject and find it by name`() {
        kTransaction.useTransaction {
            val newSubject = subjectRepository.createSubject("testSubject")
            val foundSubject = subjectRepository.findSubjectByName(newSubject.name)
            assert(foundSubject != null)
            assert(foundSubject?.name == newSubject.name)
        }
    }

    @Test
    fun `Should get all subjects`() {
        kTransaction.useTransaction {
            val newSubject1 = subjectRepository.createSubject("testSubject1")
            val newSubject2 = subjectRepository.createSubject("testSubject2")
            val subjects = subjectRepository.getAllSubjects(10, 0)
            assert(subjects.size == 2)
            assert(subjects.contains(newSubject1))
            assert(subjects.contains(newSubject2))
        }
    }


}