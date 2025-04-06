package repositories

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.users.UserRepository
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.junit.jupiter.api.assertDoesNotThrow
import org.ktorm.database.Database
import java.io.StringReader
import java.sql.Connection
import javax.sql.DataSource
import kotlin.test.*

class UserRepositoryTest {
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
            
            CREATE TABLE IF NOT EXISTS ADMIN (
                user_id INT UNIQUE NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
                primary key (user_id)
            );
        """)
        )
    }

    private val userRepository = UserRepository(database)
    private val kTransaction = KTransaction(database)
/*
    @Test
    fun `Should create a new user for every role`(){
        kTransaction.useTransaction {
            var userCount = 1
            for(role in Role.entries){
                val user = User {
                    email = "testemail${role.name}@test.com"
                    username = "tester${role.name}"
                    password = User.hashPassword("test")
                    profileImage = byteArrayOf()
                }.let { userRepository.create(it, role) }
                assertEquals(user.id, userCount)
                assertEquals(user.email, "testemail${role.name}@test.com")
                assertEquals(user.username, "tester${role.name}")
                assertTrue(User.verifyPassword(user.password, "test"))
                assertContentEquals(user.profileImage, byteArrayOf())
                assertDoesNotThrow {
                    when(role){
                        Role.STUDENT -> {
                            user.toStudent(database)
                        }
                        Role.TEACHER -> {
                            user.toTeacher(database)
                        }
                        Role.ADMIN -> {
                            user.toAdmin(database)
                        }
                        Role.TECHNICAL_SERVICE -> {
                            user.toTechnicalService(database)
                        }
                    }
                }
                userCount += 1
            }
        }
    }
*/
    @Test
    fun `Should find user my id`() {
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.create(it, Role.STUDENT) }
            val user = userRepository.findById(newUser.id)
            assertNotNull(user)
            assertEquals(newUser.id, user.id)
            assertEquals(newUser.email, user.email)
            assertEquals(newUser.username, user.username)
            assertContentEquals(newUser.profileImage, user.profileImage)
        }
    }

    @Test
    fun `Should find user my email`(){
        kTransaction.useTransaction {
            val newUser = User {
                email = "testemail@test.com"
                username = "tester"
                password = User.hashPassword("test")
                profileImage = byteArrayOf()
            }.let { userRepository.create(it, Role.STUDENT) }
            val user = userRepository.findByEmail(newUser.email)
            assertNotNull(user)
            assertEquals(newUser.id, user.id)
            assertEquals(newUser.email, user.email)
            assertEquals(newUser.username, user.username)
            assertContentEquals(newUser.profileImage, user.profileImage)
        }
    }
}