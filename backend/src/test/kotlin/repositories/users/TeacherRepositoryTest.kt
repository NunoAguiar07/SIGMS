package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.users.TeacherRepository
import isel.leic.group25.db.repositories.users.UserRepository
import repositories.DatabaseTestSetup
import kotlin.test.*

class TeacherRepositoryTest {

    private val teacherRepository = TeacherRepository(DatabaseTestSetup.database)
    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)
    private val classRepository = ClassRepository(DatabaseTestSetup.database)
    private val subjectRepository = SubjectRepository(DatabaseTestSetup.database)
    private val kTormCommand = KtormCommand(DatabaseTestSetup.database)


    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    private fun createTeacher(email: String = "teacher@test.com"): User = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("testUniversity")
        val password = User.hashPassword("test")
        return@useTransaction userRepository.createWithRole(
            email = email,
            username = "tester",
            password = password,
            role = Role.TEACHER,
            university = university,
            authProvider = "local"
        )
    }

    @Test
    fun `Should create a new teacher and find it by id`() = kTormCommand.useTransaction {
        val teacherUser = createTeacher()
        val teacher = teacherRepository.findTeacherById(teacherUser.id)
        assertNotNull(teacher)
        assertEquals(teacherUser.id, teacher.user.id)
    }

    @Test
    fun `Should return null for non-existent teacher ID`() = kTormCommand.useTransaction {
        val teacher = teacherRepository.findTeacherById(-1)
        assertNull(teacher)
    }

    @Test
    fun `Should create a new teacher and find it by email`() = kTormCommand.useTransaction {
        val teacherUser = createTeacher("findbyemail@test.com")
        val teacher = teacherRepository.findTeacherByEmail(teacherUser.email)
        assertNotNull(teacher)
        assertEquals(teacherUser.id, teacher.user.id)
    }

    @Test
    fun `Should return null for non-existent teacher email`() = kTormCommand.useTransaction {
        val teacher = teacherRepository.findTeacherByEmail("invalid@test.com")
        assertNull(teacher)
    }

    @Test
    fun `Should verify user is teacher`() = kTormCommand.useTransaction {
        val teacherUser = createTeacher("isateacher@test.com")
        assertTrue(teacherRepository.isTeacher(teacherUser))
    }

    @Test
    fun `Should verify user is not teacher`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("testUniversity")
        val user = User {
            email = "admin@test.com"
            username = "admin"
            password = User.hashPassword("test")
            profileImage = byteArrayOf()
            authProvider = "local"
            this.university = university
        }.let {
            userRepository.createWithRole(it.email, it.username, it.password, Role.ADMIN, university, it.authProvider)
        }
        assertFalse(teacherRepository.isTeacher(user))
    }

    @Test
    fun `Should find teachers by classId`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("testUniversity")
        val teacherUser1 = userRepository.createWithRole("teacher1@test.com", "t1", User.hashPassword("pass"), Role.TEACHER, university, "local")
        val teacherUser2 = userRepository.createWithRole("teacher2@test.com", "t2", User.hashPassword("pass"), Role.TEACHER, university, "local")
        val subject = subjectRepository.createSubject( "Math", university)
        val createdClass = classRepository.addClass("T1", subject)

        classRepository.addTeacherToClass(teacherUser1.toTeacher(DatabaseTestSetup.database), createdClass)
        classRepository.addTeacherToClass(teacherUser2.toTeacher(DatabaseTestSetup.database), createdClass)

        val teachers = teacherRepository.findTeachersByClassId(createdClass.id)
        assertEquals(2, teachers.size)
        assertTrue(teachers.any { it.user.id == teacherUser1.id })
        assertTrue(teachers.any { it.user.id == teacherUser2.id })
    }

    @Test
    fun `Should return empty list if no teacher assigned to class`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("testUniversity")
        val subject = subjectRepository.createSubject("Math", university)
        val createdClass = classRepository.addClass("T1", subject)
        val teachers = teacherRepository.findTeachersByClassId(createdClass.id)
        assertTrue(teachers.isEmpty())
    }
}
