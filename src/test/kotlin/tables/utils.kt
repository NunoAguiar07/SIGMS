package tables

import isel.leic.group25.db.entities.issues.IssueReport
import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.rooms.OfficeRoom
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.rooms.StudyRoom
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.entities.users.*
import isel.leic.group25.db.tables.Tables.Companion.admins
import isel.leic.group25.db.tables.Tables.Companion.classes
import isel.leic.group25.db.tables.Tables.Companion.classrooms
import isel.leic.group25.db.tables.Tables.Companion.issueReports
import isel.leic.group25.db.tables.Tables.Companion.lectures
import isel.leic.group25.db.tables.Tables.Companion.officeRooms
import isel.leic.group25.db.tables.Tables.Companion.rooms
import isel.leic.group25.db.tables.Tables.Companion.students
import isel.leic.group25.db.tables.Tables.Companion.studyRooms
import isel.leic.group25.db.tables.Tables.Companion.subjects
import isel.leic.group25.db.tables.Tables.Companion.teachers
import isel.leic.group25.db.tables.Tables.Companion.technicalServices
import isel.leic.group25.db.tables.Tables.Companion.universities
import isel.leic.group25.db.tables.Tables.Companion.users
import org.h2.jdbcx.JdbcDataSource
import org.h2.tools.RunScript
import org.ktorm.database.Database
import org.ktorm.entity.add
import java.io.StringReader
import java.sql.Connection
import javax.sql.DataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.time.Duration

class TestDatabaseHelper {
    companion object {
        // Constants for test data
        private const val TEST_UNIVERSITY_NAME = "Test University"
        private const val TEST_EMAIL_PREFIX = "testuser"
        private const val TEST_EMAIL_DOMAIN = "@test.edu"
        private const val TEST_USERNAME_PREFIX = "testuser"
        private const val TEST_PASSWORD = "testpassword123"
        private const val TEST_ROOM_NAME = "Test Room"
    }

    // Database connection properties
    private val dataSource: DataSource = JdbcDataSource().apply {
        setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL")
        user = "sa"
        password = ""
    }
    val connection: Connection = dataSource.connection
    val database: Database = Database.connect(dataSource)

    // Test data cache
    private lateinit var testUniversity: University
    private val testRooms = mutableMapOf<String, Room>()
    private val testUsers = mutableMapOf<String, User>()

    @BeforeTest
    fun setup() {
        createTables()
        testUniversity = createUniversity(TEST_UNIVERSITY_NAME)
    }

    @AfterTest
    fun cleanup() {
        clearDatabase()
        connection.close()
    }

    private fun createTables() {
        RunScript.execute(connection, StringReader("""
            CREATE TABLE IF NOT EXISTS UNIVERSITY (
                id SERIAL PRIMARY KEY,
                university_name VARCHAR(255) NOT NULL UNIQUE
            );

            CREATE TABLE IF NOT EXISTS ROOM (
                id SERIAL PRIMARY KEY,
                room_name VARCHAR(255) NOT NULL,
                capacity INT NOT NULL,
                university_id INT NOT NULL REFERENCES UNIVERSITY(id) ON DELETE CASCADE,
                CHECK(capacity > 0),
                CONSTRAINT unique_room_per_university UNIQUE (room_name, university_id)
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

            CREATE TABLE IF NOT EXISTS USERS (
                id SERIAL PRIMARY KEY,
                email VARCHAR(255) UNIQUE NOT NULL,
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                profile_image VARCHAR(255),
                auth_provider VARCHAR(50) NOT NULL CHECK (auth_provider IN ('local', 'microsoft')),
                university_id INT NOT NULL REFERENCES UNIVERSITY(id) ON DELETE CASCADE
            );

            CREATE TABLE IF NOT EXISTS STUDENT (
                user_id INT UNIQUE NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
                primary key (user_id)
            );

            CREATE TABLE IF NOT EXISTS TEACHER (
                user_id INT UNIQUE NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
                office_id INT DEFAULT NULL REFERENCES OFFICE_ROOM(id) ON DELETE SET NULL,
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
                requested_role VARCHAR(50) NOT NULL CHECK (requested_role IN ('STUDENT','TEACHER', 'TECHNICAL_SERVICE')),
                verification_token VARCHAR(255) UNIQUE NOT NULL,
                verified_by INT REFERENCES ADMINISTRATOR(user_id) ON DELETE SET NULL,
                created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                expires_at TIMESTAMP NOT NULL,
                status VARCHAR(20) DEFAULT 'PENDING' NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
            );

            CREATE TABLE IF NOT EXISTS SUBJECT (
                id SERIAL PRIMARY KEY,
                subject_name VARCHAR(255) NOT NULL,
                university_id INT NOT NULL REFERENCES UNIVERSITY(id) ON DELETE CASCADE
            );

            CREATE TABLE IF NOT EXISTS CLASS (
                id SERIAL PRIMARY KEY,
                class_name VARCHAR(255) NOT NULL,
                subject_id INT NOT NULL REFERENCES SUBJECT(id) ON DELETE CASCADE,
                CONSTRAINT unique_class_per_subject UNIQUE (class_name, subject_id)
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

            CREATE TABLE IF NOT EXISTS LECTURE (
                id SERIAL PRIMARY KEY,
                class_id INT NOT NULL REFERENCES CLASS(id) ON DELETE CASCADE,
                room_id INT NOT NULL REFERENCES CLASSROOM(id) ON DELETE CASCADE,
                class_type VARCHAR(20) CHECK (class_type IN ('theoretical', 'practical', 'theoretical_practical')),
                week_day INT CHECK(week_day > 0 AND week_day < 8),
                start_time TIME NOT NULL,
                end_time TIME NOT NULL,
                CHECK (end_time > start_time),
                CONSTRAINT unique_class_room_time UNIQUE (class_id, room_id, week_day, start_time)
            );

            CREATE TABLE IF NOT EXISTS LECTURE_CHANGE (
                lecture_id INT PRIMARY KEY REFERENCES LECTURE(id) ON DELETE CASCADE,
                original_room_id INT NOT NULL REFERENCES CLASSROOM(id) ON DELETE CASCADE,
                original_week_day INT CHECK(original_week_day > 0 AND original_week_day < 8),
                original_start_time TIME NOT NULL,
                original_end_time TIME NOT NULL,
                original_class_type VARCHAR(20) CHECK (original_class_type IN ('theoretical', 'practical', 'theoretical_practical')),
                effective_from TIMESTAMP,
                effective_until TIMESTAMP,
                CHECK (effective_until > effective_from),
                CHECK (original_end_time > original_start_time)
            );

            CREATE TABLE IF NOT EXISTS ISSUE_REPORT (
                id SERIAL PRIMARY KEY,
                room_id INT NOT NULL REFERENCES ROOM(id) ON DELETE CASCADE,
                created_by INT NOT NULL REFERENCES USERS(id) ON DELETE CASCADE,
                assigned_to INT DEFAULT NULL REFERENCES TECHNICAL_SERVICES(user_id) ON DELETE SET NULL,
                description TEXT NOT NULL
            );
        """))
    }

    private fun clearDatabase() {
        // Clear tables in proper order to respect foreign key constraints
        RunScript.execute(connection, StringReader("""
            DELETE FROM ISSUE_REPORT;
            DELETE FROM LECTURE_CHANGE;
            DELETE FROM LECTURE;
            DELETE FROM ATTEND;
            DELETE FROM TEACH;
            DELETE FROM CLASS;
            DELETE FROM SUBJECT;
            DELETE FROM ROLE_APPROVALS;
            DELETE FROM ADMINISTRATOR;
            DELETE FROM TECHNICAL_SERVICES;
            DELETE FROM TEACHER;
            DELETE FROM STUDENT;
            DELETE FROM USERS;
            DELETE FROM OFFICE_ROOM;
            DELETE FROM CLASSROOM;
            DELETE FROM STUDY_ROOM;
            DELETE FROM ROOM;
            DELETE FROM UNIVERSITY;
        """))
    }

    // Helper methods for creating test data

    fun createUniversity(name: String = TEST_UNIVERSITY_NAME): University {
        return University {
            this.name = name
        }.also {
            database.universities.add(it)
        }
    }

    fun createIssueReport(
        room: Room,
        user: User,
        description: String = "Test issue report"
    ): IssueReport {
        return IssueReport {
            this.room = room
            this.createdBy = user
            this.description = description
        }.also {
            database.issueReports.add(it)
        }
    }

    fun createRoom(
        name: String = TEST_ROOM_NAME,
        capacity: Int = 30,
        university: University = testUniversity,
        type: String = "CLASSROOM"
    ): Room {
        val newRoom = Room {
            this.name = name
            this.capacity = capacity
            this.university = university
        }.also { database.rooms.add(it) }

        when (type.uppercase()) {
            "CLASSROOM" -> Classroom { room = newRoom }.also { database.classrooms.add(it) }
            "STUDY_ROOM" -> StudyRoom { room = newRoom }.also { database.studyRooms.add(it) }
            "OFFICE_ROOM" -> OfficeRoom { room = newRoom }.also { database.officeRooms.add(it) }
        }

        testRooms[name] = newRoom
        return newRoom
    }

    fun createUser(
        emailSuffix: String = "",
        usernameSuffix: String = "",
        password: String = TEST_PASSWORD,
        university: University = testUniversity,
        authProvider: String = "local"
    ): User {
        val email = "$TEST_EMAIL_PREFIX$emailSuffix$TEST_EMAIL_DOMAIN"
        val username = "$TEST_USERNAME_PREFIX$usernameSuffix"

        return User {
            this.email = email
            this.username = username
            this.password = User.hashPassword(password)
            this.profileImage = byteArrayOf()
            this.authProvider = authProvider
            this.university = university
        }.also {
            database.users.add(it)
            testUsers[username] = it
        }
    }

    fun createStudent(user: User? = null): Student {
        val userToUse = user ?: createUser(usernameSuffix = "student")
        return Student {
            this.user = userToUse
        }.also {
            database.students.add(it)
        }
    }

    fun createTeacher(user: User? = null, office: OfficeRoom? = null): Teacher {
        val userToUse = user ?: createUser(usernameSuffix = "teacher")
        return Teacher {
            this.user = userToUse
            this.office = office
        }.also {
            database.teachers.add(it)
        }
    }

    fun createTechnicalService(user: User? = null): TechnicalService {
        val userToUse = user ?: createUser(usernameSuffix = "tech")
        return TechnicalService {
            this.user = userToUse
        }.also {
            database.technicalServices.add(it)
        }
    }

    fun createAdministrator(user: User? = null): Admin {
        val userToUse = user ?: createUser(usernameSuffix = "admin")
        return Admin {
            this.user = userToUse
        }.also {
            database.admins.add(it)
        }
    }

    fun createSubject(name: String, university: University = testUniversity): Subject {
        return Subject {
            this.name = name
            this.university = university
        }.also {
            database.subjects.add(it)
        }
    }

    fun createClass(name: String, subject: Subject): Class {
        return Class {
            this.name = name
            this.subject = subject
        }.also {
            database.classes.add(it)
        }
    }

    fun createLecture(
        schoolClass: Class,
        room: Classroom,
        classType: ClassType = ClassType.THEORETICAL,
        weekDay: WeekDay = WeekDay.MONDAY,
        startTime: Duration = Duration.parse("PT09H00M"),
        endTime: Duration = Duration.parse("PT10H00M")
    ): Lecture {
        return Lecture {
            this.schoolClass = schoolClass
            this.classroom = room
            this.type = classType
            this.weekDay = weekDay
            this.startTime = startTime
            this.endTime = endTime
        }.also {
            database.lectures.add(it)
        }
    }
}