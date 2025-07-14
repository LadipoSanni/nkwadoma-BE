ALTER TABLE meedl_notification_entity
    ALTER COLUMN content_detail TYPE TEXT USING content_detail::TEXT;

