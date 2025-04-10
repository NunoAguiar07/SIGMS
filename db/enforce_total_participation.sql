CREATE OR REPLACE FUNCTION enforce_total_participation() RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM STUDENT WHERE user_id = NEW.id
        UNION ALL
        SELECT 1 FROM TEACHER WHERE user_id = NEW.id
        UNION ALL
        SELECT 1 FROM TECHNICAL_SERVICES WHERE user_id = NEW.id
        UNION ALL
        SELECT 1 FROM ADMINISTRATOR WHERE user_id = NEW.id
    ) THEN
        RAISE EXCEPTION 'User must belong to at least one role (STUDENT, TEACHER, TECHNICAL_SERVICES, or ADMIN).';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER enforce_total_participation_trigger
    AFTER INSERT ON USERS
    DEFERRABLE INITIALLY DEFERRED
    FOR EACH ROW EXECUTE FUNCTION enforce_total_participation();