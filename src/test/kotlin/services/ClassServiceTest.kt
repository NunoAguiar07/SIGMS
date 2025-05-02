package services

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.services.ClassService
import isel.leic.group25.services.errors.ClassError
import isel.leic.group25.utils.Failure
import isel.leic.group25.utils.Success
import mocks.repositories.timetables.MockClassRepository
import mocks.repositories.timetables.MockSubjectRepository
import mocks.repositories.utils.MockTransaction
import repositories.DatabaseTestSetup
import kotlin.test.*


class ClassServiceTest {
    private val classRepository = MockClassRepository()
    private val subjectRepository = MockSubjectRepository()
    private val transactionInterface = MockTransaction()

    private val classService = ClassService(
        classRepository = classRepository,
        subjectRepository = subjectRepository,
        transactionInterface = transactionInterface
    )

    // Helper function to create test subjects
    private fun createTestSubjects(count: Int = 1): List<Subject> {
        return transactionInterface.useTransaction {
            (1..count).map { i ->
                subjectRepository.createSubject("Test Subject $i")
            }
        }
    }

    private fun createTestClasses(count: Int = 1, subject: Subject): List<Class> {
        return transactionInterface.useTransaction {
            (1..count).map { i ->
                classRepository.addClass(
                   name = "Test Class $i", subject = subject
                )
            }
        }
    }

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `getAllClassesFromSubject returns classes with default parameters`() {
        val subject = createTestSubjects()
        assertNotNull(subject[0], "Subject should not be null")
        val classes = createTestClasses(5, subject[0])
        assertNotNull(classes, "Classes should not be null")
        assertNotNull(subject[0].id, "Subject ID should not be null")
        val result = classService.getAllClassesFromSubject(subject[0].id, 10, 0)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertTrue(successResult.size == 5, "Expected 5 classes")
        assertTrue(successResult.all { it in classes }, "Expected all classes to be in the result")
    }

    @Test
    fun `getAllClassesFromSubject fails with non-existing subject ID`() {
        val result = classService.getAllClassesFromSubject(999, 5, 0)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            ClassError.SubjectNotFound,
            result.value,
            "Expected SubjectNotFound error"
        )
    }

    @Test
    fun `getClassById returns class when exists`() {
        val subject = createTestSubjects()
        assertNotNull(subject[0], "Subject should not be null")
        val classes = createTestClasses(1, subject[0])
        assertNotNull(classes, "Classes should not be null")
        assertNotNull(subject[0].id, "Subject ID should not be null")
        val result = classService.getClassById(classes[0].id)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals(classes[0].id, successResult.id, "Class ID should match")
    }

    @Test
    fun `getClassById fails with non-existing class ID`() {
        val result = classService.getClassById(999)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            ClassError.ClassNotFound,
            result.value,
            "Expected ClassNotFound error"
        )
    }

    @Test
    fun `createClass succeeds with valid parameters`() {
        val subject = createTestSubjects()
        assertNotNull(subject[0], "Subject should not be null")
        val result = classService.createClass("Test Class", subject[0].id)
        assertTrue(result is Success, "Expected Success")
        val successResult = result.value
        assertEquals("Test Class", successResult.name, "Class name should match")
    }

    @Test
    fun `createClass fails with non-existing subject ID`() {
        val result = classService.createClass("Test Class", 999)
        assertTrue(result is Failure, "Expected Failure")
        assertEquals(
            ClassError.SubjectNotFound,
            result.value,
            "Expected SubjectNotFound error"
        )
    }

    @Test
    fun `createClass fails with duplicate class name for same subject`() {
        val subject = createTestSubjects()
        assertNotNull(subject[0], "Subject should not be null")
        val class1 = classService.createClass("Test 1", subject[0].id)
        assertTrue(class1 is Success, "Expected Success")
        val class2 = classService.createClass("Test 1", subject[0].id)
        assertTrue(class2 is Failure, "Expected Failure")
        assertEquals(
            ClassError.ClassAlreadyExists,
            class2.value,
            "Expected ClassAlreadyExists error"
        )
    }

}