

-- 1. Drop old MFA-related boolean columns
ALTER TABLE meedl_user
DROP COLUMN IF EXISTS enable_phone_number_mfa,
    DROP COLUMN IF EXISTS enable_email_mfa,
    DROP COLUMN IF EXISTS mfa_enabled;

ALTER TABLE meedl_user
DROP COLUMN IF EXISTS mfa_type;
-- 3. Drop Postgres ENUM type if it exists (cleanup)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_type WHERE typname = 'mfatype') THEN
DROP TYPE mfatype;
END IF;
END$$;

-- 4. Add new column as VARCHAR to match @Enumerated(EnumType.STRING)
ALTER TABLE meedl_user
    ADD COLUMN mfa_type VARCHAR(50) DEFAULT 'MFA_DISABLED';
