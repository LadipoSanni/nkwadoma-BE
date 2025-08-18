-- V16__update_userentity_mfa.sql

-- 1. Drop old MFA-related boolean columns
ALTER TABLE meedl_user
DROP COLUMN IF EXISTS enable_phone_number_mfa,
    DROP COLUMN IF EXISTS enable_email_mfa,
    DROP COLUMN IF EXISTS mfa_enabled;

-- 2. Create new ENUM type for MFAType if it does not exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'mfatype') THEN
        DROP TYPE mfatype;
    END IF;
END$$;

-- 3. Add new column using ENUM type
ALTER TABLE meedl_user
    ADD COLUMN IF NOT EXISTS mfa_type VARCHAR(50) DEFAULT 'MFA_DISABLED';
