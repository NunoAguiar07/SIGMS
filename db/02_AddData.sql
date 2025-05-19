-- Create ISEL university
INSERT INTO UNIVERSITY (university_name)
VALUES ('Instituto Superior de Engenharia de Lisboa')
ON CONFLICT (university_name) DO NOTHING;

-- Admin user 1
INSERT INTO USERS (email, username, password, university_id)
VALUES ('admin1@example.com',
        'admin1',
        '$argon2id$v=19$m=65536,t=10,p=1$fEfMmVyC94henmE11OwjOQ$9NE8Jx69+INXRbnP+uU9nkSQUbmVj7J9QW0VPYUpUeQ',
        "local",
        (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')
        )

ON CONFLICT (email) DO NOTHING;

INSERT INTO ADMINISTRATOR (user_id)
SELECT id FROM USERS WHERE email = 'admin1@example.com'
ON CONFLICT (user_id) DO NOTHING;

-- Admin user 2
INSERT INTO USERS (email, username, password, university_id)
VALUES ('admin2@example.com',
        'admin2',
        '$argon2id$v=19$m=65536,t=10,p=1$fEfMmVyC94henmE11OwjOQ$9NE8Jx69+INXRbnP+uU9nkSQUbmVj7J9QW0VPYUpUeQ',
        "local",
        (SELECT id FROM UNIVERSITY WHERE university_name = 'Instituto Superior de Engenharia de Lisboa')
       )
ON CONFLICT (email) DO NOTHING;

INSERT INTO ADMINISTRATOR (user_id)
SELECT id FROM USERS WHERE email = 'admin2@example.com'
ON CONFLICT (user_id) DO NOTHING;