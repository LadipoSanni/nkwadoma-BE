ALTER TABLE meedl_user
    ADD COLUMN IF NOT EXISTS level_of_eduction VARCHAR(255) DEFAULT 'UNKNOWN';

UPDATE organization
    SET rc_number = registration_number
    WHERE (rc_number IS NULL OR rc_number = '')
        AND registration_number IS NOT NULL
        AND registration_number <> '';

ALTER TABLE organization
DROP COLUMN registration_number;
