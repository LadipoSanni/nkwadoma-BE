DO $$
BEGIN
    IF NOT EXISTS (
        SELECT FROM information_schema.columns
        WHERE table_name = 'meedl_notification_entity'
        AND column_name = 'sender_mail'
    ) THEN
ALTER TABLE meedl_notification_entity
    ADD COLUMN sender_mail VARCHAR(255);
END IF;
END $$;