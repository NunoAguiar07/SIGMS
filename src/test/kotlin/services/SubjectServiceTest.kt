package services

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import isel.leic.group25.services.SubjectService
import isel.leic.group25.services.errors.SubjectError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import repositories.DatabaseTestSetup
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SubjectServiceTest {
    // Test database setup
    private val subjectRepository = SubjectRepository(DatabaseTestSetup.database)
    private val transactionInterface = KTransaction(DatabaseTestSetup.database)

    private val subjectService = SubjectService(subjectRepository, transactionInterface)

    // Helper function to create test subjects
    private fun createTestSubjects(count: Int = 1): List<Subject> {
        return transactionInterface.useTransaction {
            (1..count).map { i ->
                subjectRepository.createSubject("Test Subject $i")
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
        val result = subjectService.getAllSubjects(null, null)

        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertTrue(successResult.size == 5, "Expected 5 subjects")
        assertTrue(successResult.all { it in subjects }, "Expected all subjects to be in the result")
    }

    @Test
    fun `getAllSubjects should return subjects with specified limit and offset`() {
        val subjects = createTestSubjects(10)
        val result = subjectService.getAllSubjects("5", "2")

        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertTrue(successResult.size == 5, "Expected 5 subjects")
        assertTrue(successResult.all { it in subjects.subList(2, 7) }, "Expected subjects to match the specified range")
    }

    @Test
    fun `getAllSubjects should return error for invalid limit`() {
        val upperResult = subjectService.getAllSubjects("200", null)
        val lowerResult = subjectService.getAllSubjects("0", null)

        assertTrue(upperResult is Failure, "Expected Failure")
        assertEquals(SubjectError.InvalidSubjectLimit, upperResult.value,"Expected InvalidSubjectLimit error")

        assertTrue(lowerResult is Failure, "Expected Failure")
        assertEquals(SubjectError.InvalidSubjectLimit, lowerResult.value,"Expected InvalidSubjectLimit error")
    }

    @Test
    fun `getAllSubjects should return error for invalid offset`() {
        val result = subjectService.getAllSubjects(null, "-1")

        assertTrue(result is Failure, "Expected Failure")
        assertEquals(SubjectError.InvalidSubjectOffset, result.value,"Expected InvalidSubjectOffset error")
    }

    @Test
    fun `getSubjectById should return subject when exists`() {
        val subject = createTestSubjects(1).first()
        val result = subjectService.getSubjectById(subject.id.toString())

        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(subject.id, successResult.id, "Subject ID should match")
        assertEquals(subject.name, successResult.name, "Subject name should match")
    }

    @Test
    fun `getSubjectById should return error when subject doesn't exist`() {
        val result = subjectService.getSubjectById("999")

        assertTrue(result is Failure, "Expected Failure")
        assertEquals(SubjectError.SubjectNotFound, result.value,"Expected SubjectNotFound error")
    }

    @Test
    fun `getSubjectById should return InvalidSubjectId when id is invalid`() {
        val result = subjectService.getSubjectById(null)

        assertTrue(result is Failure, "Expected Failure")
        assertEquals(SubjectError.InvalidSubjectId, result.value,"Expected InvalidSubjectId error")
    }

    @Test
    fun `createSubject should create a new subject`() {
        val result = subjectService.createSubject("PG")

        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("PG", successResult.name, "Subject name should match")
    }

    @Test
    fun `createSubject should return SubjectAlreadyExists when name exists`() {
        createTestSubjects(1)
        val result = subjectService.createSubject("Test Subject 1")

        assertTrue(result is Failure, "Expected Failure")
        assertEquals(SubjectError.SubjectAlreadyExists, result.value,"Expected SubjectAlreadyExists error")
    }

    @Test
    fun `createSubject should handle empty name`() {
        val result = subjectService.createSubject("")

        assertTrue(result is Failure, "Expected Failure")
        assertEquals(SubjectError.InvalidSubjectData, result.value,"Expected InvalidSubjectData error")
    }

    @Test
    fun `createSubject should handle long names`() {
        val longName = "a".repeat(256)
        val result = subjectService.createSubject(longName)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(SubjectError.InvalidSubjectData, result.value,"Expected InvalidSubjectData error")
    }
}