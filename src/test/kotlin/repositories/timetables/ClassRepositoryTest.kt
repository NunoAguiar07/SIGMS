package repositories

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import isel.leic.group25.db.repositories.users.UserRepository
import kotlin.test.*

class ClassRepositoryTest {
    private val kTransaction = KTransaction(DatabaseTestSetup.database)
    private val userRepository = UserRepository(DatabaseTestSetup.database)
    private val classRepository = ClassRepository(DatabaseTestSetup.database)
    private val subjectRepository = SubjectRepository(DatabaseTestSetup.database)

    @Test
    fun `Should create a new class and find it by id`() {
        kTransaction.useTransaction {
            val newSubject = Subject {
                name = "Test Subject"
            }.let { subjectRepository.createSubject(it.name) }
            val name = "Test Class"
            val clazz = classRepository.addClass(name, newSubject!!)
            val foundClass = classRepository.findClassById(clazz.id)
            assertNotNull(foundClass)
            assertEquals(clazz.id, foundClass.id)

        }
    }

    @Test
    fun `Should create a new class and find it by name`() {
        kTransaction.useTransaction {
            val newSubject = Subject {
                name = "Test Subject1"
            }.let { subjectRepository.createSubject(it.name) }
            val name = "Test Class1"
            val clazz = classRepository.addClass(name, newSubject!!)
            val foundClass = classRepository.findClassByName(clazz.name)
            assertNotNull(foundClass)
            assertEquals(clazz.id, foundClass.id)
        }
    }

    @Test
    fun `Should create a new class and find it by subject`() {
        kTransaction.useTransaction {
            val newSubject = Subject {
                name = "Test Subject2"
            }.let { subjectRepository.createSubject(it.name) }
            val name = "Test Class2"
            val clazz = classRepository.addClass(name, newSubject!!)
            val foundClasses = classRepository.findClassesBySubject(newSubject, 10, 0)
            assertNotNull(foundClasses)
            assertTrue(foundClasses.isNotEmpty())
            assertEquals(clazz.id, foundClasses[0].id)
        }
    }

    @Test
    fun `Should update a class`() {
        kTransaction.useTransaction {
            val newSubject = Subject {
                name = "Test Subject3"
            }.let { subjectRepository.createSubject(it.name) }
            val name1 = "Test Class3"
            val clazz = classRepository.addClass(name1, newSubject!!)
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
        kTransaction.useTransaction {
            val newSubject = Subject {
                name = "Test Subject4"
            }.let { subjectRepository.createSubject(it.name) }
            val name = "Test Class4"
            val clazz = classRepository.addClass(name, newSubject!!)
            val result = classRepository.deleteClassById(clazz.id)
            assertTrue(result)
            val foundClass = classRepository.findClassById(clazz.id)
            assertNull(foundClass)
        }
    }

    @Test
    fun `Should delete a class`() {
        kTransaction.useTransaction {
            val newSubject = Subject {
                name = "Test Subject5"
            }.let { subjectRepository.createSubject(it.name) }
            val name = "Test Class5"
            val clazz = classRepository.addClass(name, newSubject!!)
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
        val subject = Subject {
            name = "Test Subject6"
        }
        val newSubject = subjectRepository.createSubject(subject.name)
        val clazz = classRepository.addClass(className.name, newSubject!!)
        val newUser = User {
            email = "testemail@test.com"
            username = "tester"
            password = User.hashPassword("test")
            profileImage = byteArrayOf()
        }.let { userRepository.create(it, Role.STUDENT) }
        val result = classRepository.addStudentToClass(newUser, clazz)
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
        val subject = Subject {
            name = "Test Subject7"
        }
        val newSubject = subjectRepository.createSubject(subject.name)
        val clazz = classRepository.addClass(className.name, newSubject!!)
        val newUser = User {
            email = "teste123mail@test.com"
            username = "tester123"
            password = User.hashPassword("test")
            profileImage = byteArrayOf()
        }.let { userRepository.create(it, Role.STUDENT) }
        classRepository.addStudentToClass(newUser, clazz)
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
        val subject = Subject {
            name = "Test Subject8"
        }
        val newSubject = subjectRepository.createSubject(subject.name)
        val clazz = classRepository.addClass(className.name, newSubject!!)
        val newUser = User {
            email = "teste1234mail@test.com"
            username = "tester1234"
            password = User.hashPassword("test")
            profileImage = byteArrayOf()
        }.let { userRepository.create(it, Role.STUDENT) }
        classRepository.addStudentToClass(newUser, clazz)
        val foundClasses = classRepository.findClassesByStudentId(newUser.id)
        assertNotNull(foundClasses)
        assertTrue(foundClasses.isNotEmpty())
    }
    // need to add method to add a teacher to a class
    /*
    @Test
    fun `should find classes by teacherId`(){
        val className = Class {
            name = "Test Class8"
        }
        val subject = Subject {
            name = "Test Subject8"
        }
        val newSubject = subjectRepository.createSubject(subject.name)
        val clazz = classRepository.addClass(className.name, newSubject!!)
        val newUser = User {
            email = "teste1234mail@test.com"
            username = "tester1234"
            password = User.hashPassword("test")
            profileImage = byteArrayOf()
        }.let { userRepository.create(it, Role.TEACHER) }
        classRepository.addStudentToClass(newUser, clazz)
    }*/


}