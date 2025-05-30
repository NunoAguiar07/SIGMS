-- Create ISEL university
INSERT INTO UNIVERSITY (university_name)
VALUES ('Instituto Superior de Engenharia de Lisboa')
ON CONFLICT (university_name) DO NOTHING;

-- Admin user 1
INSERT INTO USERS (email, username, password, auth_provider, university_id)
VALUES ('admin1@example.com',
        'admin1',
        '$argon2id$v=19$m=65536,t=10,p=1$eZVzlw07wQTuWdt7QCCxBw$HPjI9u1HhCei8lkWbESS6G6q+Cgbp0GMxNNGT8Y0GZY',
        'local',
        (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')
        )

ON CONFLICT (email) DO NOTHING;

INSERT INTO ADMINISTRATOR (user_id)
SELECT id FROM USERS WHERE email = 'admin1@example.com'
ON CONFLICT (user_id) DO NOTHING;

-- Admin user 2
INSERT INTO USERS (email, username, password, auth_provider, university_id)
VALUES ('admin2@example.com',
        'admin2',
        '$argon2id$v=19$m=65536,t=10,p=1$eZVzlw07wQTuWdt7QCCxBw$HPjI9u1HhCei8lkWbESS6G6q+Cgbp0GMxNNGT8Y0GZY',
        'local',
        (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')
       )
ON CONFLICT (email) DO NOTHING;

INSERT INTO ADMINISTRATOR (user_id)
SELECT id FROM USERS WHERE email = 'admin2@example.com'
ON CONFLICT (user_id) DO NOTHING;

-- Add subjects for ISEL
INSERT INTO SUBJECT (subject_name, university_id)
VALUES
    ('Calculus', (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Linear Algebra', (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Physics', (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Programming Fundamentals', (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Database Systems', (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Computer Networks', (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Software Engineering', (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Operating Systems', (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Artificial Intelligence', (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Distributed Systems', (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa'))
ON CONFLICT (subject_name, university_id) DO NOTHING;

-- Add classes for each subject
-- Calculus classes
INSERT INTO CLASS (class_name, subject_id)
VALUES
    ('T1', (SELECT id FROM SUBJECT WHERE subject_name = 'Calculus')),
    ('T2', (SELECT id FROM SUBJECT WHERE subject_name = 'Calculus'))
ON CONFLICT (class_name, subject_id) DO NOTHING;

-- Linear Algebra classes
INSERT INTO CLASS (class_name, subject_id)
VALUES
    ('T1', (SELECT id FROM SUBJECT WHERE subject_name = 'Linear Algebra')),
    ('T2', (SELECT id FROM SUBJECT WHERE subject_name = 'Linear Algebra'))
ON CONFLICT (class_name, subject_id) DO NOTHING;

-- Physics classes
INSERT INTO CLASS (class_name, subject_id)
VALUES
    ('T1', (SELECT id FROM SUBJECT WHERE subject_name = 'Physics')),
    ('T2', (SELECT id FROM SUBJECT WHERE subject_name = 'Physics'))
ON CONFLICT (class_name, subject_id) DO NOTHING;

-- Programming Fundamentals classes
INSERT INTO CLASS (class_name, subject_id)
VALUES
    ('T1', (SELECT id FROM SUBJECT WHERE subject_name = 'Programming Fundamentals')),
    ('T2', (SELECT id FROM SUBJECT WHERE subject_name = 'Programming Fundamentals')),
    ('T3', (SELECT id FROM SUBJECT WHERE subject_name = 'Programming Fundamentals'))
ON CONFLICT (class_name, subject_id) DO NOTHING;

-- Database Systems classes
INSERT INTO CLASS (class_name, subject_id)
VALUES
    ('T1', (SELECT id FROM SUBJECT WHERE subject_name = 'Database Systems')),
    ('T2', (SELECT id FROM SUBJECT WHERE subject_name = 'Database Systems'))
ON CONFLICT (class_name, subject_id) DO NOTHING;

-- Computer Networks classes
INSERT INTO CLASS (class_name, subject_id)
VALUES
    ('T1', (SELECT id FROM SUBJECT WHERE subject_name = 'Computer Networks')),
    ('T2', (SELECT id FROM SUBJECT WHERE subject_name = 'Computer Networks'))
ON CONFLICT (class_name, subject_id) DO NOTHING;

-- Software Engineering classes
INSERT INTO CLASS (class_name, subject_id)
VALUES
    ('T1', (SELECT id FROM SUBJECT WHERE subject_name = 'Software Engineering')),
    ('T2', (SELECT id FROM SUBJECT WHERE subject_name = 'Software Engineering'))
ON CONFLICT (class_name, subject_id) DO NOTHING;

-- Operating Systems classes
INSERT INTO CLASS (class_name, subject_id)
VALUES
    ('T1', (SELECT id FROM SUBJECT WHERE subject_name = 'Operating Systems')),
    ('T2', (SELECT id FROM SUBJECT WHERE subject_name = 'Operating Systems'))
ON CONFLICT (class_name, subject_id) DO NOTHING;

-- Artificial Intelligence classes
INSERT INTO CLASS (class_name, subject_id)
VALUES
    ('T1', (SELECT id FROM SUBJECT WHERE subject_name = 'Artificial Intelligence')),
    ('T2', (SELECT id FROM SUBJECT WHERE subject_name = 'Artificial Intelligence'))
ON CONFLICT (class_name, subject_id) DO NOTHING;

-- Distributed Systems classes
INSERT INTO CLASS (class_name, subject_id)
VALUES
    ('T1', (SELECT id FROM SUBJECT WHERE subject_name = 'Distributed Systems')),
    ('T2', (SELECT id FROM SUBJECT WHERE subject_name = 'Distributed Systems'))
ON CONFLICT (class_name, subject_id) DO NOTHING;

-- Insert Classrooms for ISEL
INSERT INTO ROOM (room_name, capacity, university_id)
VALUES
    ('A1', 30, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('A2', 30, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('A3', 25, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('B1', 40, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('B2', 40, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('B3', 35, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('C1', 50, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('C2', 50, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('D1', 20, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('D2', 20, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Auditório 1', 100, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Auditório 2', 80, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Lab Informática 1', 25, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Lab Informática 2', 25, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Lab Eletrónica', 20, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa'))
ON CONFLICT (room_name, university_id) DO NOTHING;

-- Add these rooms to CLASSROOM table
INSERT INTO CLASSROOM (id)
SELECT id FROM ROOM WHERE room_name IN (
                                        'A1', 'A2', 'A3', 'B1', 'B2', 'B3', 'C1', 'C2', 'D1', 'D2',
                                        'Auditório 1', 'Auditório 2', 'Lab Informática 1', 'Lab Informática 2', 'Lab Eletrónica'
    )
ON CONFLICT (id) DO NOTHING;

-- Insert Study Rooms for ISEL
INSERT INTO ROOM (room_name, capacity, university_id)
VALUES
    ('Sala Estudo 1', 10, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Sala Estudo 2', 10, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Sala Estudo 3', 8, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Sala Estudo 4', 8, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Sala Estudo 5', 6, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Sala Grupo 1', 15, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Sala Grupo 2', 15, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Sala Silêncio 1', 5, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Sala Silêncio 2', 5, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa'))
ON CONFLICT (room_name, university_id) DO NOTHING;

-- Add these rooms to STUDY_ROOM table
INSERT INTO STUDY_ROOM (id)
SELECT id FROM ROOM WHERE room_name LIKE 'Sala Estudo%'
                       OR room_name LIKE 'Sala Grupo%'
                       OR room_name LIKE 'Sala Silêncio%'
ON CONFLICT (id) DO NOTHING;

-- Insert Office Rooms for ISEL
INSERT INTO ROOM (room_name, capacity, university_id)
VALUES
    ('Gab. Professor 1', 1, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Gab. Professor 2', 1, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Gab. Professor 3', 1, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Gab. Professor 4', 1, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Gab. Professor 5', 1, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Gab. Direção', 3, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Gab. Secretaria', 4, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Gab. Administração', 4, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Sala Reuniões 1', 10, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')),
    ('Sala Reuniões 2', 8, (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa'))
ON CONFLICT (room_name, university_id) DO NOTHING;

-- Add these rooms to OFFICE_ROOM table
INSERT INTO OFFICE_ROOM (id)
SELECT id FROM ROOM WHERE room_name LIKE 'Gab.%' OR room_name LIKE 'Sala Reuniões%'
ON CONFLICT (id) DO NOTHING;

INSERT INTO LECTURE (class_id, room_id, class_type, week_day, start_time, end_time)
VALUES (
           (SELECT id FROM CLASS WHERE class_name = 'T1' AND subject_id = (SELECT id FROM SUBJECT WHERE subject_name = 'Calculus')),
           (SELECT id FROM CLASSROOM WHERE id = (SELECT id FROM ROOM WHERE room_name = 'A1')),
           'theoretical',
           1,
           '09:00',
           '10:30'
       );

INSERT INTO LECTURE (class_id, room_id, class_type, week_day, start_time, end_time)
VALUES (
           (SELECT id FROM CLASS WHERE class_name = 'T2' AND subject_id = (SELECT id FROM SUBJECT WHERE subject_name = 'Linear Algebra')),
           (SELECT id FROM CLASSROOM WHERE id = (SELECT id FROM ROOM WHERE room_name = 'A2')),
           'theoretical',
           2,
           '11:00',
           '12:30'
       );

INSERT INTO LECTURE (class_id, room_id, class_type, week_day, start_time, end_time)
VALUES (
           (SELECT id FROM CLASS WHERE class_name = 'T1' AND subject_id = (SELECT id FROM SUBJECT WHERE subject_name = 'Programming Fundamentals')),
           (SELECT id FROM CLASSROOM WHERE id = (SELECT id FROM ROOM WHERE room_name = 'Lab Informática 1')),
           'practical',
           3,
           '10:00',
           '12:00'
       );

INSERT INTO LECTURE (class_id, room_id, class_type, week_day, start_time, end_time)
VALUES (
           (SELECT id FROM CLASS WHERE class_name = 'T2' AND subject_id = (SELECT id FROM SUBJECT WHERE subject_name = 'Physics')),
           (SELECT id FROM CLASSROOM WHERE id = (SELECT id FROM ROOM WHERE room_name = 'B1')),
           'theoretical',
           4,
           '08:30',
           '10:00'
       );

INSERT INTO LECTURE (class_id, room_id, class_type, week_day, start_time, end_time)
VALUES (
           (SELECT id FROM CLASS WHERE class_name = 'T1' AND subject_id = (SELECT id FROM SUBJECT WHERE subject_name = 'Computer Networks')),
           (SELECT id FROM CLASSROOM WHERE id = (SELECT id FROM ROOM WHERE room_name = 'C1')),
           'theoretical_practical',
           5,
           '14:00',
           '16:00'
       );