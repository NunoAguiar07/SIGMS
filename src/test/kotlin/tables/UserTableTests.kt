package tables

import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.tables.Tables.Companion.admins
import isel.leic.group25.db.tables.Tables.Companion.students
import isel.leic.group25.db.tables.Tables.Companion.teachers
import isel.leic.group25.db.tables.Tables.Companion.technicalServices
import isel.leic.group25.db.tables.Tables.Companion.users
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertNull
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.first
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.update
import kotlin.test.*

class UserTableTests {
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
    fun `Should create and add a new user and retrieve it from the database`() {
        val newUser = dbHelper.createUser(
            emailSuffix = "123",
            usernameSuffix = "123",
            password = "supersecretpassword123"
        )

        val retrievedUser = database.users.firstOrNull { it.id eq newUser.id }
        assertNotNull(retrievedUser, "User should exist in database")
        assertEquals(newUser.id, retrievedUser.id)
    }

    @Test
    fun `Should create a new user and login afterwards`() {
        val testPassword = "supersecretpassword123"
        val newUser = dbHelper.createUser(
            emailSuffix = "69",
            usernameSuffix = "123",
            password = testPassword
        )

        val retrievedUser = database.users.find { it.email eq newUser.email }
        assertNotNull(retrievedUser, "User should exist in database")
        assertEquals(newUser.email, retrievedUser.email)
        assertEquals(newUser.username, retrievedUser.username)

        assertTrue(
            User.verifyPassword(retrievedUser.password, testPassword),
            "Password verification should succeed"
        )

        assertDoesNotThrow {
            val loginSuccess = User.verifyPassword(retrievedUser.password, testPassword)
            assertTrue(loginSuccess, "Login with correct credentials should succeed")
        }

        assertFalse(
            User.verifyPassword(retrievedUser.password, "wrongpassword"),
            "Login with wrong password should fail"
        )
    }

    @Test
    fun `Should retrieve a user, modify it and save it to the database`() {
        val user = dbHelper.createUser(
            emailSuffix = "456",
            usernameSuffix = "456",
            password = "supersecretpassword123"
        )

        user.email = "newtestemail@email.com"
        database.users.update(user)

        val retrievedUser = database.users.firstOrNull { it.id eq user.id }
        assertNotNull(retrievedUser, "User should exist in database")
        assertEquals(user.id, retrievedUser.id)
        assertEquals(user.email, retrievedUser.email)
    }

    @Test
    fun `Should delete a user from the database`() {
        val user = dbHelper.createUser(
            emailSuffix = "789",
            usernameSuffix = "789",
            password = "supersecretpassword123"
        )

        user.delete()

        assertNull(database.users.firstOrNull { it.id eq user.id })
    }

    @Test
    fun `Should create an admin user`() {
        val userInfo = dbHelper.createUser(
            emailSuffix = "admin",
            usernameSuffix = "admin",
            password = "supersecretpassword123"
        )

        val admin = dbHelper.createAdministrator(userInfo)

        val retrievedAdmin = database.admins.first { it.user eq userInfo.id }
        assertEquals(retrievedAdmin.user.id, admin.user.id)
    }

    @Test
    fun `Should create a teacher user`() {
        val userInfo = dbHelper.createUser(
            emailSuffix = "teacher",
            usernameSuffix = "teacher",
            password = "supersecretpassword123"
        )

        val teacher = dbHelper.createTeacher(userInfo)

        val retrievedTeacher = database.teachers.firstOrNull { it.user eq userInfo.id }
        assertNotNull(retrievedTeacher, "Teacher should exist in database")
        assertEquals(retrievedTeacher.user.id, teacher.user.id)
    }

    @Test
    fun `Should create a student user`() {
        val userInfo = dbHelper.createUser(
            emailSuffix = "student",
            usernameSuffix = "student",
            password = "supersecretpassword123"
        )

        val student = dbHelper.createStudent(userInfo)

        val retrievedStudent = database.students.firstOrNull { it.user eq userInfo.id }
        assertNotNull(retrievedStudent, "Student should exist in database")
        assertEquals(retrievedStudent.user.id, student.user.id)
    }

    @Test
    fun `Should create a technical services user`() {
        val userInfo = dbHelper.createUser(
            emailSuffix = "tech",
            usernameSuffix = "tech",
            password = "supersecretpassword123"
        )

        val technicalService = dbHelper.createTechnicalService(userInfo)

        val retrievedTechnicalServices = database.technicalServices.firstOrNull { it.user eq userInfo.id }
        assertNotNull(retrievedTechnicalServices, "Technical service should exist in database")
        assertEquals(retrievedTechnicalServices.user.id, technicalService.user.id)
    }
}