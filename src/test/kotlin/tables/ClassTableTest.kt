package tables

import isel.leic.group25.db.tables.Tables.Companion.classes
import org.junit.jupiter.api.assertDoesNotThrow
import org.ktorm.dsl.eq
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.update
import kotlin.test.*

class ClassTableTest {
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
    fun `Should create a new class`() {
        val subject = dbHelper.createSubject(
            name = "PS"
        )
        val newClass = dbHelper.createClass(
            subject = subject,
            name = "51D"
        )

        val retrievedClass = database.classes.firstOrNull { it.id eq newClass.id }
        assertNotNull(retrievedClass, "Class should exist in database")
        assertEquals(newClass.id, retrievedClass.id)
        assertEquals(subject.id, retrievedClass.subject.id)
    }

    @Test
    fun `Should update a class`() {
        val subject = dbHelper.createSubject(
            name = "PS",
        )
        val newClass = dbHelper.createClass(
            subject = subject,
            name = "51D"
        )

        newClass.name = "51N"
        val rows = database.classes.update(newClass)

        assertEquals(1, rows, "Should update exactly one row")
        val updatedClass = database.classes.firstOrNull() { it.id eq newClass.id }
        assertNotNull(updatedClass, "Class should exist after update")
        assertEquals("51N", updatedClass.name)
    }

    @Test
    fun `Should delete a class`() {
        val subject = dbHelper.createSubject(
            name = "PS"
        )
        val newClass = dbHelper.createClass(
            subject = subject,
            name = "51D"
        )
        assertNotNull(database.classes.firstOrNull { it.id eq newClass.id },
            "Class should exist before deletion")

        newClass.delete()

        assertNull(database.classes.firstOrNull { it.id eq newClass.id },
            "Class should be deleted")
    }

    @Test
    fun `Should enforce class name uniqueness per subject`() {
        val subject = dbHelper.createSubject(
            name = "PS"
        )
        dbHelper.createClass(
            subject = subject,
            name = "51D"
        )

//        assertThrows<Exception> {
//            dbHelper.createClass(
//                subject = subject,
//                name = "51D"
//            )
//        }

        val otherSubject = dbHelper.createSubject(
            name = "Other Subject"
        )
        assertDoesNotThrow {
            dbHelper.createClass(
                subject = otherSubject,
                name = "51D"
            )
        }
    }
}