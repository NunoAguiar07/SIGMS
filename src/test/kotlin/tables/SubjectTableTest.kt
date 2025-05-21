package tables

import isel.leic.group25.db.tables.Tables.Companion.subjects
import org.ktorm.dsl.eq
import org.ktorm.entity.firstOrNull
import kotlin.test.*

class SubjectTableTest {
    private val dbHelper = TestDatabaseHelper()
    private val database get() = dbHelper.database

    @BeforeTest
    fun setup() {
        dbHelper.setup()
    }

    @AfterTest
    fun cleanup() {
        dbHelper.cleanup()
    }

    @Test
    fun `Should create a new subject`(){
        val newSubject = dbHelper.createSubject(
            name = "PS"
        )

        val subject = database.subjects.firstOrNull{ it.id eq newSubject.id }
        assertNotNull(subject, "Subject should exist in database")
        assertEquals(subject.id, newSubject.id)
        assertEquals(subject.name, newSubject.name)
        assertEquals(subject.name, "PS")
        assertEquals(subject.university.id, newSubject.university.id)
        assertEquals(subject.university.name, newSubject.university.name)
    }

    @Test
    fun `Should update a subject`(){
        val newSubject = dbHelper.createSubject(
            name = "PS"
        )
        newSubject.name = "PDM"
        newSubject.flushChanges()

        val subject = database.subjects.firstOrNull{ it.id eq newSubject.id }
        assertNotNull(subject, "Subject should exist in database")
        assertEquals(subject.id, newSubject.id)
        assertEquals(subject.name, newSubject.name)
        assertEquals(subject.name, "PDM")
    }

    @Test
    fun `Should delete a subject`(){
        val newSubject = dbHelper.createSubject(
            name = "PS"
        )
        newSubject.delete()
        assertNull(database.subjects.firstOrNull { it.id eq newSubject.id })
    }
}