package services

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.services.SubjectService
import isel.leic.group25.services.errors.SubjectError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.MockRepositories
import org.ktorm.database.Database
import repositories.DatabaseTestSetup
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SubjectServiceTest {
    private val mockDB = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        password = ""
    )
    private val mockRepositories = MockRepositories(mockDB)
    private val subjectService = SubjectService(mockRepositories, mockRepositories.ktormCommand)

    private fun createTestSubjects(count: Int = 1): List<Subject> {
        return mockRepositories.ktormCommand.useTransaction {
            (1..count).map { i ->
                val university = mockRepositories.from({universityRepository}){
                    createUniversity("Test University $i")
                }
                mockRepositories.from({subjectRepository}){
                    createSubject("Test Subject $i", university)
                }
            }
        }
    }

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }


    @Test
    fun `getAllSubjects should return subjects with default limit and offset`() {
        val subjects = createTestSubjects(5)
        val result = subjectService.getAllSubjects(10, 0)

        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertTrue(successResult.size == 5, "Expected 5 subjects")
        assertTrue(successResult.all { it in subjects }, "Expected all subjects to be in the result")
    }

    @Test
    fun `getAllSubjects should return subjects with specified limit and offset`() {
        val subjects = createTestSubjects(10)
        val result = subjectService.getAllSubjects(5, 2)

        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertTrue(successResult.size == 5, "Expected 5 subjects")
        assertTrue(successResult.all { it in subjects.subList(2, 7) }, "Expected subjects to match the specified range")
    }

    @Test
    fun `getSubjectById should return subject when exists`() {
        val subject = createTestSubjects(1).first()
        val result = subjectService.getSubjectById(subject.id)

        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(subject.id, successResult.id, "Subject ID should match")
        assertEquals(subject.name, successResult.name, "Subject name should match")
    }

    @Test
    fun `getSubjectById should return error when subject doesn't exist`() {
        val result = subjectService.getSubjectById(999)

        assertTrue(result is Failure, "Expected Failure")
        assertEquals(SubjectError.SubjectNotFound, result.value,"Expected SubjectNotFound error")
    }

    @Test
    fun `createSubject should create a new subject`() {
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val result = subjectService.createSubject("PG", university.id)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("PG", successResult.name, "Subject name should match")
    }

    @Test
    fun `createSubject should return SubjectAlreadyExists when name exists`() {
        createTestSubjects(1)
        val university = mockRepositories.from({universityRepository}){
            createUniversity("Test University")
        }
        val result = subjectService.createSubject("Test Subject 1", university.id)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(SubjectError.SubjectAlreadyExists, result.value,"Expected SubjectAlreadyExists error")
    }

    @Test
    fun `deleteSubject should succeed when subject exists`() {
        val subject = createTestSubjects(1)[0]
        val result = subjectService.deleteSubject(subject.id)

        assertTrue(result is Success)
        assertEquals(true, result.value, "Expected deletion to succeed")
    }

    @Test
    fun `deleteSubject should fail when subject does not exist`() {
        val result = subjectService.deleteSubject(999)

        assertTrue(result is Failure)
        assertEquals(SubjectError.SubjectNotFound, result.value, "Expected SubjectNotFound error")
    }

    @Test
    fun `getAllSubjectsByUniversity should return subjects for given university`() {
        val university = mockRepositories.from({universityRepository}) {
            createUniversity("University A")
        }
        subjectService.createSubject("Math", university.id)
        subjectService.createSubject("Physics", university.id)

        val result = subjectService.getAllSubjectsByUniversity(university.id, 10, 0)

        assertTrue(result is Success)
        assertEquals(2, result.value.size)
        assertTrue(result.value.all { it.university.id == university.id })
    }

    @Test
    fun `getAllSubjectsByUniversity should return error for invalid university`() {
        val result = subjectService.getAllSubjectsByUniversity(999, 10, 0)

        assertTrue(result is Failure)
        assertEquals(SubjectError.UniversityNotFound, result.value)
    }

    @Test
    fun `getSubjectsByNameAndUniversityId should return filtered subjects`() {
        val university = mockRepositories.from({universityRepository}) {
            createUniversity("Filtered University")
        }
        subjectService.createSubject("Algorithms", university.id)
        subjectService.createSubject("Algebra", university.id)
        subjectService.createSubject("Physics", university.id)

        val result = subjectService.getSubjectsByNameAndUniversityId(university.id, "Alg", 10, 0)

        assertTrue(result is Success)
        val matchedNames = result.value.map { it.name }
        assertEquals(setOf("Algorithms", "Algebra"), matchedNames.toSet())
    }

    @Test
    fun `getSubjectsByNameAndUniversityId should fail for non-existing university`() {
        val result = subjectService.getSubjectsByNameAndUniversityId(999, "Any", 10, 0)

        assertTrue(result is Failure)
        assertEquals(SubjectError.UniversityNotFound, result.value)
    }
}