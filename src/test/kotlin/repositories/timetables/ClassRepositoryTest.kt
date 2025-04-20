package repositories

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import isel.leic.group25.db.repositories.users.UserRepository
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.ktorm.database.Database
import java.io.StringReader
import java.sql.Connection
import javax.sql.DataSource
import kotlin.test.*

class DatabaseTestSetup {
    companion object {
        val database: Database
        val connection: Connection


        @AfterTest
        fun clearDB(){
            RunScript.execute(connection, StringReader("""
                DELETE FROM USERS;
                DELETE FROM STUDENT;
                DELETE FROM TEACHER;
                DELETE FROM TECHNICAL_SERVICES;
                DELETE FROM ADMINISTRATOR;
                DELETE FROM SUBJECT;
                DELETE FROM CLASS;
                DELETE FROM TEACH;
                DELETE FROM ATTEND;
                DELETE FROM ROOM;
                DELETE FROM STUDY_ROOM;
                DELETE FROM CLASSROOM;
                DELETE FROM OFFICE_ROOM;
                DELETE FROM LECTURE;
                DELETE FROM ISSUE_REPORT;
            """)
            )
        }


        init {
            val dataSource: DataSource = JdbcDataSource().apply {
                setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
                user = "sa"
                password = ""
            }
            connection = dataSource.connection
            database = Database.connect(dataSource)

            RunScript.execute(connection, StringReader("""
                CREATE TABLE IF NOT EXISTS USERS (
                    id SERIAL PRIMARY KEY,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    profile_image VARCHAR(255)
                );
                CREATE TABLE IF NOT EXISTS STUDENT (
    user_id INT UNIQUE NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
    primary key (user_id)

);

CREATE TABLE IF NOT EXISTS TEACHER (
     user_id INT UNIQUE NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
     primary key (user_id)
);

CREATE TABLE IF NOT EXISTS TECHNICAL_SERVICES (
    user_id INT UNIQUE NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
    primary key (user_id)
);

CREATE TABLE IF NOT EXISTS ADMINISTRATOR (
    user_id INT UNIQUE NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
    primary key (user_id)
);

CREATE TABLE IF NOT EXISTS SUBJECT (
     id SERIAL PRIMARY KEY,
     subject_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS CLASS (
    id SERIAL PRIMARY KEY,
    class_name VARCHAR(255) NOT NULL,
    subject_id INT NOT NULL REFERENCES SUBJECT(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS TEACH (
    teacher_id INT NOT NULL REFERENCES TEACHER(user_id) ON DELETE CASCADE,
    class_id INT NOT NULL REFERENCES CLASS(id) ON DELETE CASCADE,
    PRIMARY KEY (teacher_id, class_id)
);

CREATE TABLE IF NOT EXISTS ATTEND (
    student_id INT NOT NULL REFERENCES STUDENT(user_id) ON DELETE CASCADE,
    class_id INT NOT NULL REFERENCES CLASS(id) ON DELETE CASCADE,
    PRIMARY KEY (student_id, class_id)
);

CREATE TABLE IF NOT EXISTS ROOM (
    id SERIAL PRIMARY KEY,
    room_name VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    CHECK(capacity > 0)
);

CREATE TABLE IF NOT EXISTS STUDY_ROOM (
    id SERIAL PRIMARY KEY REFERENCES ROOM(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS CLASSROOM (
    id SERIAL PRIMARY KEY REFERENCES ROOM(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS OFFICE_ROOM (
    id SERIAL PRIMARY KEY REFERENCES ROOM(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS LECTURE (
    class_id INT NOT NULL REFERENCES CLASS(id) ON DELETE CASCADE,
    room_id INT NOT NULL REFERENCES ROOM(id) ON DELETE CASCADE,
    class_type VARCHAR(20) CHECK (class_type IN ('theoretical', 'practical', 'theoretical_practical')),
    week_day int CHECK(week_day > 0 and week_day < 8),
    start_time int NOT NULL,
    end_time int NOT NULL,
    CHECK (end_time > start_time)
);

CREATE TABLE IF NOT EXISTS ISSUE_REPORT (
    id SERIAL PRIMARY KEY,
    room_id INT NOT NULL REFERENCES ROOM(id) ON DELETE CASCADE,
    description TEXT NOT NULL
);
                
                
            """))
        }


    }
}


// Test class example
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
    /*
    class ClassRepository(private val database: Database): ClassRepositoryInterface {


    override fun findClassesByTeacherId(userId: Int): List<Class> {
        return database.teachersClasses.filter { it.teacherId eq userId }.map { it.schoolClass }
    }
}
     */
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