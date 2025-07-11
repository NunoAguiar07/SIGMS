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
    profile_image text,
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
     university_id INT NOT NULL REFERENCES UNIVERSITY(id) ON DELETE CASCADE,
    CONSTRAINT unique_subject_per_university UNIQUE (subject_name, university_id)
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
    class_type VARCHAR(30) CHECK (class_type IN ('theoretical', 'practical', 'theoretical_practical')),
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
    original_class_type VARCHAR(25) CHECK (original_class_type IN ('theoretical', 'practical', 'theoretical_practical')),
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

