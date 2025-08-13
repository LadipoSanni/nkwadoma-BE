DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='meedl_user'
          AND column_name='mfa_phone_number'
    ) THEN
ALTER TABLE meedl_user
    ADD COLUMN mfa_phone_number VARCHAR(255);
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='meedl_user'
          AND column_name='enable_phone_number_mfa'
    ) THEN
ALTER TABLE meedl_user
    ADD COLUMN enable_phone_number_mfa BOOLEAN NOT NULL DEFAULT FALSE;
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='meedl_user'
          AND column_name='enable_email_mfa'
    ) THEN
ALTER TABLE meedl_user
    ADD COLUMN enable_email_mfa BOOLEAN NOT NULL DEFAULT FALSE;
END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='meedl_user'
          AND column_name='mfa_enabled'
    ) THEN
ALTER TABLE meedl_user
    ADD COLUMN mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE;
END IF;
END
$$;
