package repositories.timetables

import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import repositories.DatabaseTestSetup
import kotlin.test.*

class SubjectRepositoryTest {
    private val kTormCommand = KtormCommand(DatabaseTestSetup.database)
    private val subjectRepository = SubjectRepository(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `Should create a new subject and find it by id`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Software Engineering", university)
        val found = subjectRepository.findSubjectById(subject.id)

        assertNotNull(found)
        assertEquals(subject.name, found.name)
        assertEquals(subject.university.id, found.university.id)
    }

    @Test
    fun `Should create a new subject and find it by name`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("Test University")
        val subject = subjectRepository.createSubject("Databases", university)
        val found = subjectRepository.findSubjectByName("Databases")

        assertNotNull(found)
        assertEquals(subject.id, found.id)
    }

    @Test
    fun `Should get all subjects`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("Test University")
        val subj1 = subjectRepository.createSubject("Networks", university)
        val subj2 = subjectRepository.createSubject("Operating Systems", university)

        val allSubjects = subjectRepository.getAllSubjects(limit = 10, offset = 0)

        assertTrue(allSubjects.any { it.id == subj1.id })
        assertTrue(allSubjects.any { it.id == subj2.id })
    }

    @Test
    fun `Should get subjects by university id`() = kTormCommand.useTransaction {
        val uni1 = universityRepository.createUniversity("Uni1")
        val uni2 = universityRepository.createUniversity("Uni2")

        val subj1 = subjectRepository.createSubject("Compilers", uni1)
        subjectRepository.createSubject("AI", uni2)

        val result = subjectRepository.getAllSubjectsByUniversityId(uni1.id, limit = 10, offset = 0)

        assertEquals(1, result.size)
        assertEquals(subj1.id, result.first().id)
    }

    @Test
    fun `Should get subjects by partial name and university`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("Test University")
        val subj1 = subjectRepository.createSubject("Advanced Programming", university)
        subjectRepository.createSubject("Linear Algebra", university)

        val result = subjectRepository.getSubjectsByNameAndUniversityId(
            universityId = university.id,
            subjectPartialName = "Programming",
            limit = 10,
            offset = 0
        )

        assertEquals(1, result.size)
        assertEquals(subj1.name, result.first().name)
    }

    @Test
    fun `Should delete an existing subject successfully`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("Delete University")
        val subject = subjectRepository.createSubject("To Be Deleted", university)

        val deleted = subjectRepository.deleteSubject(subject.id)

        assertTrue(deleted)
        assertNull(subjectRepository.findSubjectById(subject.id))
    }

    @Test
    fun `Should return false when deleting a non-existent subject`() = kTormCommand.useTransaction {
        val result = subjectRepository.deleteSubject(id = 999999)
        assertFalse(result)
    }

    @Test
    fun `Should paginate correctly`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("Paginated University")
        subjectRepository.createSubject("Subject1", university)
        subjectRepository.createSubject("Subject2", university)
        subjectRepository.createSubject("Subject3", university)

        val page1 = subjectRepository.getAllSubjects(limit = 2, offset = 0)
        val page2 = subjectRepository.getAllSubjects(limit = 2, offset = 2)

        assertEquals(2, page1.size)
        assertEquals(1, page2.size)
        assertNotEquals(page1.first().id, page2.first().id)
    }
}
