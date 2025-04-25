package repositories

import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.ktorm.database.Database
import java.io.StringReader
import java.sql.Connection
import javax.sql.DataSource
import kotlin.test.AfterTest

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
                DELETE FROM ROLE_APPROVALS;
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

CREATE TABLE IF NOT EXISTS ROLE_APPROVALS (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
    requested_role VARCHAR(50) NOT NULL CHECK (requested_role IN ('STUDENT', 'TEACHER', 'TECHNICAL_SERVICE')),
    verification_token VARCHAR(255) UNIQUE NOT NULL,
    verified_by INT REFERENCES ADMINISTRATOR(user_id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
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
    start_time time NOT NULL,
    end_time time NOT NULL,
    CHECK (end_time > start_time)
);

CREATE TABLE IF NOT EXISTS ISSUE_REPORT (
    id SERIAL PRIMARY KEY,
    room_id INT NOT NULL REFERENCES ROOM(id) ON DELETE CASCADE,
    description TEXT NOT NULL
);
                
                
            """)
            )
        }


    }
}