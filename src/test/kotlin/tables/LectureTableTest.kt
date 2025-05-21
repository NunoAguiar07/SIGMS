package tables

import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.tables.Tables.Companion.classrooms
import isel.leic.group25.db.tables.Tables.Companion.lectures
import isel.leic.group25.utils.toHoursAndMinutes
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.assertNull
import org.ktorm.dsl.eq
import org.ktorm.entity.first
import org.ktorm.entity.firstOrNull
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class LectureTableTest {
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
    fun `Should create a new lecture`() {
        val newSubject = dbHelper.createSubject(
            name = "PS"
        )
        val newClass = dbHelper.createClass(
            subject = newSubject,
            name = "51D"
        )
        val newRoom = dbHelper.createRoom(
            type = "Classroom",
            name = "G.2.02",
            capacity = 15
        )
        val classRoom = database.classrooms.firstOrNull { it.id eq newRoom.id }
        assertNotNull(classRoom, "Classroom should exist in database")
        val newLecture = dbHelper.createLecture(
            room = classRoom,
            schoolClass = newClass,
            classType = ClassType.PRACTICAL,
            startTime = Duration.parse("11h30m"),
            endTime = Duration.parse("11h30m") + 90.minutes
        )
        val lecture = database.lectures.firstOrNull { it.classId eq newLecture.schoolClass.id }
        assertNotNull(lecture, "Lecture should exist in database")
        assertEquals(newLecture.schoolClass.id, lecture.schoolClass.id)
        assertEquals(newLecture.classroom.room.id, lecture.classroom.room.id)
        assertEquals(newLecture.duration, lecture.duration)
        assertEquals(newLecture.weekDay, lecture.weekDay)
        assertEquals(newLecture.startTime.toHoursAndMinutes(), "11:30")
        assertEquals(newLecture.endTime.toHoursAndMinutes(), "13:00")
    }

    @Test
    fun `Should update a lecture`() {
        val newSubject = dbHelper.createSubject(
            name = "PS"
        )
        val newClass = dbHelper.createClass(
            subject = newSubject,
            name = "51D"
        )
        val newRoom = dbHelper.createRoom(
            type = "Classroom",
            name = "G.2.02",
            capacity = 15
        )
        val classRoom = database.classrooms.firstOrNull { it.id eq newRoom.id }
        assertNotNull(classRoom, "Classroom should exist in database")
        val newLecture = dbHelper.createLecture(
            room = classRoom,
            schoolClass = newClass,
            classType = ClassType.PRACTICAL,
            startTime = Duration.parse("11h30m"),
            endTime = Duration.parse("11h30m") + 90.minutes
        )
        val lecture = database.lectures.first { it.classId eq newLecture.schoolClass.id }
        assertEquals(newLecture.schoolClass.id, lecture.schoolClass.id)
        assertEquals(newLecture.classroom.room.id, lecture.classroom.room.id)
        assertEquals(newLecture.duration, lecture.duration)
        newLecture.endTime += Duration.parse("1h")
        newLecture.type = ClassType.THEORETICAL
        newLecture.flushChanges()
        val changedLecture = database.lectures.first { it.classId eq newLecture.schoolClass.id }
        assertEquals(newLecture.schoolClass.id, changedLecture.schoolClass.id)
        assertEquals(newLecture.classroom.room.id, changedLecture.classroom.room.id)
        assertEquals(newLecture.type, changedLecture.type)
        assertEquals(Duration.parse("2h 30m"), changedLecture.duration)
    }

    @Test
    fun `Should delete a lecture`() {
        val newSubject = dbHelper.createSubject(
            name = "PS"
        )
        val newClass = dbHelper.createClass(
            subject = newSubject,
            name = "51D"
        )
        val newRoom = dbHelper.createRoom(
            type = "Classroom",
            name = "G.2.02",
            capacity = 15
        )
        val newClassRoom = database.classrooms.firstOrNull { it.id eq newRoom.id }
        assertNotNull(newClassRoom, "Classroom should exist in database")
        val newLecture = dbHelper.createLecture(
            room = newClassRoom,
            schoolClass = newClass,
            classType = ClassType.PRACTICAL,
            startTime = Duration.parse("11h30m"),
            endTime = Duration.parse("11h30m") + 90.minutes
        )
        newLecture.delete()
        assertNull( database.lectures.firstOrNull { it.classId eq newLecture.schoolClass.id } )
    }
}