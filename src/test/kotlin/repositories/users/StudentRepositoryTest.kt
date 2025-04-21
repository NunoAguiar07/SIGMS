package repositories.users

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.users.StudentRepository
import isel.leic.group25.db.repositories.users.UserRepository
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.ktorm.database.Database
import java.io.StringReader
import java.sql.Connection
import javax.sql.DataSource
import kotlin.test.*

class StudentRepositoryTest {
    private val connection: Connection
    private val database: Database

    @AfterTest
    fun clearDB(){
        RunScript.execute(connection, StringReader("""
            DELETE FROM USERS;
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
        """)
        )
    }

    private val studentRepository = StudentRepository(database)
    private val userRepository = UserRepository(database)
    private val kTransaction = KTransaction(database)

    @Test
    fun `Should create a new student and find it by id`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.create(it, Role.STUDENT) }
            val student = studentRepository.findStudentById(newUser.id)
            assertNotNull(student)
            assertEquals(newUser.id, student.user.id)
        }
    }

    @Test
    fun `Should create a new student and find it by email`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.create(it, Role.STUDENT) }
            val student = studentRepository.findStudentByEmail(newUser.email)
            assertNotNull(student)
            assertEquals(newUser.id, student.user.id)
        }
    }

    @Test
    fun `Should verify user is student`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.create(it, Role.STUDENT) }
            assertTrue(studentRepository.isStudent(newUser))
        }
    }

    @Test
    fun `Should verify user is not student`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.create(it, Role.ADMIN) }
            assertFalse(studentRepository.isStudent(newUser))
        }
    }
}