-- Add cohort_loanee_id column to loanee_loan_detail table
ALTER TABLE loanee_loan_detail ADD COLUMN cohort_loanee_id UUID;

-- Update existing data to populate cohort_loanee_id in loanee_loan_detail
UPDATE loanee_loan_detail lld
SET cohort_loanee_id = cl.id
    FROM cohort_loanee cl
WHERE cl.loanee_loan_detail_id = lld.id;

-- Add foreign key constraint
ALTER TABLE loanee_loan_detail
    ADD CONSTRAINT fk_loanee_loan_detail_cohort_loanee
        FOREIGN KEY (cohort_loanee_id) REFERENCES cohort_loanee(id);

-- Add unique constraint to enforce one-to-one relationship
ALTER TABLE loanee_loan_detail
    ADD CONSTRAINT uk_loanee_loan_detail_cohort_loanee UNIQUE (cohort_loanee_id);

-- Remove the loanee_loan_detail_id column from cohort_loanee table
ALTER TABLE cohort_loanee DROP COLUMN IF EXISTS loanee_loan_detail_id;