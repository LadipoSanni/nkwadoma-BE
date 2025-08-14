-- VXX__add_privacy_policy_accepted_to_financier_entity.sql
ALTER TABLE financier_entity
ADD COLUMN IF NOT EXISTS privacy_policy_accepted BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE meedl_user
SET email = LOWER(email)
WHERE email IS NOT NULL;