ALTER TABLE meedl_notification_entity
    ALTER COLUMN content_detail TYPE TEXT USING content_detail::TEXT;

ALTER TABLE meedl_notification_entity
DROP CONSTRAINT meedl_notification_entity_notification_flag_check;