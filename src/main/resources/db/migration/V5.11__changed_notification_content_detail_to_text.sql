-- V3__change_content_detail_to_text.sql

ALTER TABLE meedl_notification_entity
ALTER COLUMN content_detail TYPE TEXT USING content_detail::TEXT;
