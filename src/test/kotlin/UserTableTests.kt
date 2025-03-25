import isel.leic.group25.db.entities.users.*
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.ktorm.database.Database
import java.sql.Connection
import javax.sql.DataSource
import isel.leic.group25.db.tables.Tables.Companion.admins
import isel.leic.group25.db.tables.Tables.Companion.students
import isel.leic.group25.db.tables.Tables.Companion.teachers
import isel.leic.group25.db.tables.Tables.Companion.technicalServices
import isel.leic.group25.db.tables.Tables.Companion.users
import org.junit.jupiter.api.assertNull
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.first
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.update
import kotlin.test.Test
import kotlin.test.assertEquals

class UserTablesTest {
    private lateinit var connection: Connection
    private lateinit var database: Database

    init {
        val dataSource: DataSource = JdbcDataSource().apply {
            setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
            user = "sa"
            password = ""
        }
        connection = dataSource.connection
        database = Database.connect(dataSource)
        RunScript.execute(connection, java.io.StringReader("""
            CREATE TABLE IF NOT EXISTS USERS (
                id SERIAL PRIMARY KEY,
                email VARCHAR(255) UNIQUE NOT NULL,
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                profile_image VARCHAR(255)
            );
            
            CREATE TABLE IF NOT EXISTS STUDENT (
                user_id INT UNIQUE NOT NULL REFERENCES USERS(id),
                primary key (user_id)
            );
            
            CREATE TABLE IF NOT EXISTS TEACHER (
                 user_id INT UNIQUE NOT NULL REFERENCES USERS(id),
                 primary key (user_id)
            );
            
            CREATE TABLE IF NOT EXISTS TECHNICAL_SERVICES (
                user_id INT UNIQUE NOT NULL REFERENCES USERS(id),
                primary key (user_id)
            );
            
            CREATE TABLE IF NOT EXISTS ADMIN (
                user_id INT UNIQUE NOT NULL REFERENCES USERS(id),
                primary key (user_id)
            );
        """))
    }

    @Test
    fun `Should create a add a new user and retrieve it from the database`(){
        val newUser = User{
            email = "testemail@email.com"
            username = "jhondoe123"
            password = User.hashPassword("supersecretpassword123")
            profileImage = byteArrayOf()
        }

        database.users.add(newUser)

        val retrievedUser = database.users.first { it.id eq newUser.id }
        assertEquals(newUser.id, retrievedUser.id)
    }

    @Test
    fun `Should retrieve a user modify it and save it to the database`(){
        val user = User{
            email = "testemail2@email.com"
            username = "jhondoe456"
            password = User.hashPassword("supersecretpassword123")
            profileImage = byteArrayOf()
        }
        database.users.add(user)
        user.email = "newtestemail@email.com"
        database.users.update(user)
        val retrievedUser = database.users.first { it.id eq user.id }
        assertEquals(user.id, retrievedUser.id)
        assertEquals(user.email, retrievedUser.email)
    }

    @Test
    fun `Should delete a user from the database`(){
        val user = User{
            email = "testemail3@email.com"
            username = "jhondoe789"
            password = User.hashPassword("supersecretpassword123")
            profileImage = byteArrayOf()
        }
        database.users.add(user)
        user.delete()
        assertNull(database.users.firstOrNull { it.id eq user.id })
    }

    @Test
    fun `Should create an admin user`(){
        val userInfo = User{
            email = "testemail4@email.com"
            username = "jhondoe789"
            password = User.hashPassword("supersecretpassword123")
            profileImage = byteArrayOf()
        }
        database.users.add(userInfo)
        val admin = Admin{
            user = userInfo
        }
        database.admins.add(admin)
        val retrievedAdmin = database.admins.first { it.user eq userInfo.id }
        assertEquals(retrievedAdmin.user.id, admin.user.id)
    }

    @Test
    fun `Should create a teacher user`(){
        val userInfo = User{
            email = "testemail5@email.com"
            username = "jhondoe101112"
            password = User.hashPassword("supersecretpassword123")
            profileImage = byteArrayOf()
        }
        database.users.add(userInfo)
        val teacher = Teacher{
            user = userInfo
        }
        database.teachers.add(teacher)
        val retrievedTeacher = database.teachers.first { it.user eq userInfo.id }
        assertEquals(retrievedTeacher.user.id, teacher.user.id)
    }

    @Test
    fun `Should create a student user`(){
        val userInfo = User{
            email = "testemail6@email.com"
            username = "jhondoe131415"
            password = User.hashPassword("supersecretpassword123")
            profileImage = byteArrayOf()
        }
        database.users.add(userInfo)
        val student = Student{
            user = userInfo
        }
        database.students.add(student)
        val retrievedStudent = database.students.first { it.user eq userInfo.id }
        assertEquals(retrievedStudent.user.id, student.user.id)
    }

    @Test
    fun `Should create a technical services user`(){
        val userInfo = User{
            email = "testemail7@email.com"
            username = "jhondoe161718"
            password = User.hashPassword("supersecretpassword123")
            profileImage = byteArrayOf()
        }
        database.users.add(userInfo)
        val technicalService = TechnicalService{
            user = userInfo
        }
        database.technicalServices.add(technicalService)
        val retrievedTechnicalServices = database.technicalServices.first { it.user eq userInfo.id }
        assertEquals(retrievedTechnicalServices.user.id, technicalService.user.id)
    }
}