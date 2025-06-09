CREATE OR REPLACE FUNCTION apply_lecture_changes() RETURNS void AS $$
BEGIN
    -- Apply lecture changes: replace data in LECTURE with that from LECTURE_CHANGE
    UPDATE LECTURE l
    SET room_id = lc.original_room_id,
        week_day = lc.original_week_day,
        start_time = lc.original_start_time,
        end_time = lc.original_end_time,
        class_type = lc.original_class_type
    FROM LECTURE_CHANGE lc
    WHERE l.id = lc.lecture_id
      AND lc.effective_from <= now()
      AND lc.effective_until > now();

    -- Revert expired changes
    UPDATE LECTURE l
    SET room_id = lc.original_room_id,
        week_day = lc.original_week_day,
        start_time = lc.original_start_time,
        end_time = lc.original_end_time,
        class_type = lc.original_class_type
    FROM LECTURE_CHANGE lc
    WHERE l.id = lc.lecture_id
      AND lc.effective_until <= now();

    -- Delete expired changes
    DELETE FROM LECTURE_CHANGE
    WHERE effective_until <= now();
END;
$$ LANGUAGE plpgsql;