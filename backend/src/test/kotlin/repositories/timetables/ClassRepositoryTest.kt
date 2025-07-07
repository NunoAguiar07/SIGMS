package repositories

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.users.StudentRepository
import isel.leic.group25.db.repositories.users.TeacherRepository
import isel.leic.group25.db.repositories.users.UserRepository
import kotlin.test.*

class ClassRepositoryTest {
    private val kTormCommand = KtormCommand(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)
    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val studentRepository = StudentRepository(DatabaseTestSetup.database)
    private val teacherRepository = TeacherRepository(DatabaseTestSetup.database)
    private val classRepository = ClassRepository(DatabaseTestSetup.database)
    private val subjectRepository = SubjectRepository(DatabaseTestSetup.database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `Should create a new class and find it by id`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val newSubject = Subject {
                name = "Test Subject"
                university = newUniversity
            }.let { subjectRepository.createSubject(it.name, it.university) }
            val name = "Test Class"
            val clazz = classRepository.addClass(name, newSubject)
            val foundClass = classRepository.findClassById(clazz.id)
            assertNotNull(foundClass)
            assertEquals(clazz.id, foundClass.id)

        }
    }

    @Test
    fun `Should create a new class and find it by name`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val newSubject = Subject {
                name = "Test Subject1"
                university = newUniversity
            }.let { subjectRepository.createSubject(it.name, it.university) }
            val name = "Test Class1"
            val clazz = classRepository.addClass(name, newSubject)
            val foundClass = classRepository.findClassByName(clazz.name)
            assertNotNull(foundClass)
            assertEquals(clazz.id, foundClass.id)
        }
    }

    @Test
    fun `Should create a new class and find it by subject`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val newSubject = Subject {
                name = "Test Subject2"
                university = newUniversity
            }.let { subjectRepository.createSubject(it.name, it.university) }
            val name = "Test Class2"
            val clazz = classRepository.addClass(name, newSubject)
            val foundClasses = classRepository.findClassesBySubject(newSubject, 10, 0)
            assertNotNull(foundClasses)
            assertTrue(foundClasses.isNotEmpty())
            assertEquals(clazz.id, foundClasses[0].id)
        }
    }

    @Test
    fun `Should update a class`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val newSubject = Subject {
                name = "Test Subject3"
                university = newUniversity
            }.let { subjectRepository.createSubject(it.name, it.university) }
            val name1 = "Test Class3"
            val clazz = classRepository.addClass(name1, newSubject)
            clazz.name = "Updated Class"
            clazz.subject = newSubject
            val result = classRepository.updateClass(clazz)
            assertTrue(result)
            val foundClass = classRepository.findClassById(clazz.id)
            assertNotNull(foundClass)
            assertEquals("Updated Class", foundClass.name)
        }
    }

    @Test
    fun `Should delete a class by id`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val newSubject = Subject {
                name = "Test Subject4"
                university = newUniversity
            }.let { subjectRepository.createSubject(it.name, it.university) }
            val name = "Test Class4"
            val clazz = classRepository.addClass(name, newSubject)
            val result = classRepository.deleteClassById(clazz.id)
            assertTrue(result)
            val foundClass = classRepository.findClassById(clazz.id)
            assertNull(foundClass)
        }
    }

    @Test
    fun `Should delete a class`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val newSubject = Subject {
                name = "Test Subject5"
                university = newUniversity
            }.let { subjectRepository.createSubject(it.name, it.university) }
            val name = "Test Class5"
            val clazz = classRepository.addClass(name, newSubject)
            val result = classRepository.deleteClass(clazz)
            assertTrue(result)
            val foundClass = classRepository.findClassById(clazz.id)
            assertNull(foundClass)
        }
    }
    @Test
    fun `should add a student to a class` (){
        val className = Class {
            name = "Test Class6"
        }
        val newUniversity = universityRepository.createUniversity("Test University")
        val subject = Subject {
            name = "Test Subject6"
            university = newUniversity
        }
        val newSubject = subjectRepository.createSubject(subject.name, subject.university)
        val clazz = classRepository.addClass(className.name, newSubject)
        val newUser = User {
            email = "testemail@test.com"
            username = "tester"
            password = User.hashPassword("test")
            profileImage = byteArrayOf()
            authProvider = "local"
            university = newUniversity
        }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university, it.authProvider) }
        val student = studentRepository.findStudentById(newUser.id)
        assertNotNull(student)
        val result = classRepository.addStudentToClass(student, clazz)
        assertTrue(result)
        val foundClasses = classRepository.findClassesByStudentId(newUser.id)
        assertNotNull(foundClasses)
        assertTrue(foundClasses.isNotEmpty())
        assertEquals(clazz.id, foundClasses[0].id)
    }

    @Test
    fun `should remove a student from a class`(){
        val className = Class {
            name = "Test Class7"
        }
        val newUniversity = universityRepository.createUniversity("Test University")
        val subject = Subject {
            name = "Test Subject7"
            university = newUniversity
        }
        val newSubject = subjectRepository.createSubject(subject.name, subject.university)
        val clazz = classRepository.addClass(className.name, newSubject)
        val newUser = User {
            email = "teste123mail@test.com"
            username = "tester123"
            password = User.hashPassword("test")
            profileImage = byteArrayOf()
            authProvider = "local"
            university = newUniversity
        }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university, it.authProvider) }
        val student = studentRepository.findStudentById(newUser.id)
        assertNotNull(student)
        classRepository.addStudentToClass(student, clazz)
        val result = classRepository.removeStudentFromClass(newUser, clazz)
        assertTrue(result)
        val foundClasses = classRepository.findClassesByStudentId(newUser.id)
        assertNotNull(foundClasses)
        assertTrue(foundClasses.isEmpty())
    }

    @Test
    fun `should find classes by userId`(){
        val className = Class {
            name = "Test Class8"
        }
        val newUniversity = universityRepository.createUniversity("Test University")
        val subject = Subject {
            name = "Test Subject8"
        }
        val newSubject = subjectRepository.createSubject(subject.name, newUniversity)
        val clazz = classRepository.addClass(className.name, newSubject)
        val newUser = User {
            email = "teste1234mail@test.com"
            username = "tester1234"
            password = User.hashPassword("test")
            profileImage = byteArrayOf()
            authProvider = "local"
            university = newUniversity
        }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university, it.authProvider) }
        val student = studentRepository.findStudentById(newUser.id)
        assertNotNull(student)
        classRepository.addStudentToClass(student, clazz)
        val foundClasses = classRepository.findClassesByStudentId(newUser.id)
        assertNotNull(foundClasses)
        assertTrue(foundClasses.isNotEmpty())
    }

    @Test
    fun `Should add a teacher to a class`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val subject = subjectRepository.createSubject("Subject1", newUniversity)
            val clazz = classRepository.addClass("Class with Teacher", subject)

            val user = User {
                email = "teacher1@test.com"
                username = "teacher1"
                password = User.hashPassword("pass")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.TEACHER, it.university, it.authProvider) }

            val teacher = teacherRepository.findTeacherById(user.id)
            assertNotNull(teacher)
            val result = classRepository.addTeacherToClass(teacher, clazz)
            assertTrue(result)

            val classes = classRepository.findClassesByTeacherId(user.id)
            assertTrue(classes.any { it.id == clazz.id })
        }
    }

    @Test
    fun `Should remove a teacher from a class`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val subject = subjectRepository.createSubject("Subject2", newUniversity)
            val clazz = classRepository.addClass("Removable Class", subject)

            val user = User {
                email = "teacher2@test.com"
                username = "teacher2"
                password = User.hashPassword("pass")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.TEACHER, it.university, it.authProvider) }

            val teacher = teacherRepository.findTeacherById(user.id)
            assertNotNull(teacher)
            classRepository.addTeacherToClass(teacher, clazz)
            val removed = classRepository.removeTeacherFromClass(user, clazz)
            assertTrue(removed)
            val classes = classRepository.findClassesByTeacherId(user.id)
            assertTrue(classes.none { it.id == clazz.id })
        }
    }

    @Test
    fun `Should verify student is in class`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val subject = subjectRepository.createSubject("Subject3", newUniversity)
            val clazz = classRepository.addClass("Enrolled Class", subject)

            val user = User {
                email = "student@test.com"
                username = "student"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university, it.authProvider) }

            val student = studentRepository.findStudentById(user.id)
            assertNotNull(student)
            classRepository.addStudentToClass(student, clazz)

            val isInClass = classRepository.checkStudentInClass(user.id, clazz.id)
            assertTrue(isInClass)
        }
    }

    @Test
    fun `Should verify teacher is in class`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val subject = subjectRepository.createSubject("Subject4", newUniversity)
            val clazz = classRepository.addClass("Teaching Class", subject)

            val user = User {
                email = "teacher@test.com"
                username = "teacher"
                password = User.hashPassword("pass")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.TEACHER, it.university, it.authProvider) }

            val teacher = teacherRepository.findTeacherById(user.id)
            assertNotNull(teacher)
            classRepository.addTeacherToClass(teacher, clazz)

            val isInClass = classRepository.checkTeacherInClass(user.id, clazz.id)
            assertTrue(isInClass)
        }
    }

    @Test
    fun `Should find students by class ID`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val subject = subjectRepository.createSubject("Subject5", newUniversity)
            val clazz = classRepository.addClass("Class with Students", subject)

            val user = User {
                email = "student2@test.com"
                username = "student2"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.STUDENT, it.university, it.authProvider) }

            val student = studentRepository.findStudentById(user.id)
            assertNotNull(student)
            classRepository.addStudentToClass(student, clazz)

            val students = classRepository.findStudentsByClassId(clazz.id)
            assertTrue(students.any { it.user.id == student.user.id })
        }
    }

    @Test
    fun `Should find teachers by class ID`() {
        kTormCommand.useTransaction {
            val newUniversity = universityRepository.createUniversity("Test University")
            val subject = subjectRepository.createSubject("Subject6", newUniversity)
            val clazz = classRepository.addClass("Class with Teachers", subject)

            val user = User {
                email = "teacher3@test.com"
                username = "teacher3"
                password = User.hashPassword("pass")
                profileImage = byteArrayOf()
                authProvider = "local"
                university = newUniversity
            }.let { userRepository.createWithRole(it.email, it.username, it.password, Role.TEACHER, it.university, it.authProvider) }

            val teacher = teacherRepository.findTeacherById(user.id)
            assertNotNull(teacher)
            classRepository.addTeacherToClass(teacher, clazz)

            val teachers = classRepository.findTeachersByClassId(clazz.id)
            assertTrue(teachers.any { it.user.id == teacher.user.id })
        }
    }




}