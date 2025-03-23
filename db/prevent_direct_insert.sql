CREATE OR REPLACE FUNCTION prevent_direct_insert() RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'Direct inserts into this table are not allowed. Insert via the USER table.';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER prevent_direct_insert_student
    BEFORE INSERT ON STUDENT
    FOR EACH ROW EXECUTE FUNCTION prevent_direct_insert();

CREATE TRIGGER prevent_direct_insert_teacher
    BEFORE INSERT ON TEACHER
    FOR EACH ROW EXECUTE FUNCTION prevent_direct_insert();

CREATE TRIGGER prevent_direct_insert_technical_services
    BEFORE INSERT ON TECHNICAL_SERVICES
    FOR EACH ROW EXECUTE FUNCTION prevent_direct_insert();

CREATE TRIGGER prevent_direct_insert_admin
    BEFORE INSERT ON ADMIN
    FOR EACH ROW EXECUTE FUNCTION prevent_direct_insert();