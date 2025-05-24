-- Create ISEL university
INSERT INTO UNIVERSITY (university_name)
VALUES ('Instituto Superior de Engenharia de Lisboa')
ON CONFLICT (university_name) DO NOTHING;

-- Admin user 1
INSERT INTO USERS (email, username, password, auth_provider, university_id)
VALUES ('admin1@example.com',
        'admin1',
        '$argon2id$v=19$m=65536,t=10,p=1$fEfMmVyC94henmE11OwjOQ$9NE8Jx69+INXRbnP+uU9nkSQUbmVj7J9QW0VPYUpUeQ',
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
        '$argon2id$v=19$m=65536,t=10,p=1$fEfMmVyC94henmE11OwjOQ$9NE8Jx69+INXRbnP+uU9nkSQUbmVj7J9QW0VPYUpUeQ',
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