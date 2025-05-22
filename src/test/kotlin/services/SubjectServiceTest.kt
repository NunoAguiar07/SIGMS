package services

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.services.SubjectService
import isel.leic.group25.services.errors.SubjectError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.MockRepositories
import mocks.repositories.timetables.MockSubjectRepository
import mocks.repositories.timetables.MockUniversityRepository
import mocks.repositories.utils.MockTransaction
import org.ktorm.database.Database
import repositories.DatabaseTestSetup
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SubjectServiceTest {
    // Test database setup
    val mockDB = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        password = ""
    )
    private val mockRepositories = MockRepositories(mockDB)

    private val subjectService = SubjectService(mockRepositories, mockRepositories.ktormCommand)

    // Helper function to create test subjects
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
}