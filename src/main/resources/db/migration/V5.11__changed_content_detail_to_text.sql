ALTER TABLE meedl_notification_entity
    ALTER COLUMN content_detail TYPE TEXT USING content_detail::TEXT;

 DO $$
BEGIN
    IF EXISTS (
       SELECT 1
       FROM pg_constraint
       WHERE conname = 'meedl_notification_entity_notification_flag_check'
    ) THEN
        ALTER TABLE meedl_notification_entity
        DROP CONSTRAINT meedl_notification_entity_notification_flag_check;
    END IF;
END$$;