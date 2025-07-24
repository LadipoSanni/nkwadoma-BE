-- Adding a temporary column to store user_id
ALTER TABLE identity_verification_failure_record_entity
    ADD COLUMN temp_user_id VARCHAR(255);

-- Updating temp_user_id with user_id from UserEntity by joining through related tables
UPDATE identity_verification_failure_record_entity ivfre
SET temp_user_id = ue.id
    FROM loan_referral_entity lre
JOIN cohort_loanee_entity cle ON cle.id = lre.cohort_loanee_id
    JOIN loanee_entity le ON le.id = cle.loanee_id
    JOIN meedl_user ue ON ue.id = le.user_identity_id
WHERE ivfre.referral_id = lre.id;

-- Dropping the original referral_id column
ALTER TABLE identity_verification_failure_record_entity
DROP COLUMN referral_id;

-- Renaming the temporary column to user_id
ALTER TABLE identity_verification_failure_record_entity
    RENAME COLUMN temp_user_id TO user_id;
