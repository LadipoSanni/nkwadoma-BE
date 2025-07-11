--Adding new column for loan_referral_entity
ALTER TABLE loan_referral_entity
    ADD COLUMN cohort_loanee_id VARCHAR(255);


-- Update the cohort_loanee_id to match the corresponding cohort_loanee_entity.id
UPDATE loan_referral_entity lr
SET cohort_loanee_id = (
    SELECT cle.id
    FROM cohort_loanee_entity cle
    WHERE cle.loanee_id = lr.loanee_entity_id
    )
WHERE lr.loanee_entity_id IS NOT NULL;

-- Drop the existing foreign key constraint on loanee_entity_id
ALTER TABLE loan_referral_entity
    DROP CONSTRAINT IF EXISTS fk_loan_referral_loanee;

-- Dropping the old loanee_id column
ALTER TABLE loan_referral_entity
    DROP COLUMN loanee_entity_id;

-- Add new foreign key constraint to cohort_loanee_entity
ALTER TABLE loan_referral_entity
    ADD CONSTRAINT fk_loan_referral_cohort_loanee
        FOREIGN KEY (cohort_loanee_id) REFERENCES cohort_loanee_entity(id);