CREATE OR REPLACE FUNCTION check_lecture_conflict()
    RETURNS TRIGGER AS $$
BEGIN
    -- Check if there's any overlapping lecture in the same room on the same day
    IF EXISTS (
        SELECT 1 FROM LECTURE
        WHERE room_id = NEW.room_id
          AND week_day = NEW.week_day
          AND (
            -- New lecture starts during an existing lecture
            (NEW.start_time >= start_time AND NEW.start_time < end_time) OR
                -- New lecture ends during an existing lecture
            (NEW.end_time > start_time AND NEW.end_time <= end_time) OR
                -- New lecture completely contains an existing lecture
            (NEW.start_time <= start_time AND NEW.end_time >= end_time)
            )
          -- Exclude the current lecture when updating
          AND NOT (TG_OP = 'UPDATE' AND class_id = OLD.class_id AND room_id = OLD.room_id
            AND class_type = OLD.class_type AND week_day = OLD.week_day
            AND start_time = OLD.start_time AND end_time = OLD.end_time)
    ) THEN
        RAISE EXCEPTION 'Lecture conflict: Another lecture already exists in this room at the specified time';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER prevent_lecture_conflict_insert
    BEFORE INSERT ON LECTURE
    FOR EACH ROW
EXECUTE FUNCTION check_lecture_conflict();

CREATE TRIGGER prevent_lecture_conflict_update
    BEFORE UPDATE ON LECTURE
    FOR EACH ROW
EXECUTE FUNCTION check_lecture_conflict();