-- Admin user 1
INSERT INTO USERS (email, username, password)
VALUES ('admin1@example.com', 'admin1', '$argon2id$v=19$m=65536,t=10,p=1$fEfMmVyC94henmE11OwjOQ$9NE8Jx69+INXRbnP+uU9nkSQUbmVj7J9QW0VPYUpUeQ') --
ON CONFLICT (email) DO NOTHING;

INSERT INTO ADMINISTRATOR (user_id)
SELECT id FROM USERS WHERE email = 'admin1@example.com'
ON CONFLICT (user_id) DO NOTHING;

-- Admin user 2
INSERT INTO USERS (email, username, password)
VALUES ('admin2@example.com', 'admin2', '$argon2id$v=19$m=65536,t=10,p=1$fEfMmVyC94henmE11OwjOQ$9NE8Jx69+INXRbnP+uU9nkSQUbmVj7J9QW0VPYUpUeQ')
ON CONFLICT (email) DO NOTHING;

INSERT INTO ADMINISTRATOR (user_id)
SELECT id FROM USERS WHERE email = 'admin2@example.com'
ON CONFLICT (user_id) DO NOTHING;