import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.ktorm.database.Database
import java.sql.Connection
import javax.sql.DataSource
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.tables.Tables.Companion.users
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.first
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
            CREATE TABLE USERS (
                id SERIAL PRIMARY KEY,
                email VARCHAR(255) UNIQUE NOT NULL,
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                profile_image VARCHAR(255)
            );
            
            CREATE TABLE STUDENT (
                user_id INT UNIQUE NOT NULL REFERENCES USERS(id),
                primary key (user_id)
            );
            
            CREATE TABLE TEACHER (
                 user_id INT UNIQUE NOT NULL REFERENCES USERS(id),
                 primary key (user_id)
            );
            
            CREATE TABLE TECHNICAL_SERVICES (
                user_id INT UNIQUE NOT NULL REFERENCES USERS(id),
                primary key (user_id)
            );
            
            CREATE TABLE ADMIN (
                user_id INT UNIQUE NOT NULL REFERENCES USERS(id),
                primary key (user_id)
            );
        """))
    }

    @Test
    fun addUserToDatabaseAndRetrieveIt(){
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
}