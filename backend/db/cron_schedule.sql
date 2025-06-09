CREATE EXTENSION IF NOT EXISTS pg_cron;

SELECT cron.schedule(
               'lecture_change_cron',
               '0 2 * * *',  -- 2:00 AM daily
               $$CALL apply_lecture_changes();$$
       );