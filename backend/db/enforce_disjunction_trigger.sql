CREATE OR REPLACE FUNCTION enforce_disjunction() RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM STUDENT WHERE user_id = NEW.user_id
        UNION ALL
        SELECT 1 FROM TEACHER WHERE user_id = NEW.user_id
        UNION ALL
        SELECT 1 FROM TECHNICAL_SERVICES WHERE user_id = NEW.user_id
        UNION ALL
        SELECT 1 FROM ADMINISTRATOR WHERE user_id = NEW.user_id
    ) THEN
        RAISE EXCEPTION 'User already belongs to another role.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER enforce_disjunction_student
    BEFORE INSERT ON STUDENT
    FOR EACH ROW EXECUTE FUNCTION enforce_disjunction();

CREATE TRIGGER enforce_disjunction_teacher
    BEFORE INSERT ON TEACHER
    FOR EACH ROW EXECUTE FUNCTION enforce_disjunction();

CREATE TRIGGER enforce_disjunction_technical_services
    BEFORE INSERT ON TECHNICAL_SERVICES
    FOR EACH ROW EXECUTE FUNCTION enforce_disjunction();

CREATE TRIGGER enforce_disjunction_admin
    BEFORE INSERT ON ADMINISTRATOR
    FOR EACH ROW EXECUTE FUNCTION enforce_disjunction();