CREATE OR REPLACE FUNCTION process_lecture_changes()
    RETURNS void AS $$
BEGIN
    UPDATE LECTURE_CHANGE SET remaining_weeks = remaining_weeks - 1;
    DELETE FROM LECTURE_CHANGE WHERE remaining_weeks = 0;
END;
$$ LANGUAGE plpgsql;