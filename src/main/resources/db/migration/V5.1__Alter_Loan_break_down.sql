-- Adding new column for cohort_loanee_id
ALTER TABLE loanee_loan_breakdown_entity
    ADD COLUMN cohort_loanee_id VARCHAR(255);

-- Updating cohort_loanee_id by matching loanee_id with cohort_loanee_entity
UPDATE loanee_loan_breakdown_entity llb
SET cohort_loanee_id = (
    SELECT cle.id
    FROM cohort_loanee_entity cle
    WHERE cle.loanee_id = llb.loanee_id
)
WHERE llb.loanee_id IS NOT NULL;

-- Dropping the foreign key constraint for loanee_id (adjust constraint name as needed)
ALTER TABLE loanee_loan_breakdown_entity
DROP CONSTRAINT IF EXISTS fk_loanee_loan_breakdown_loanee;

-- Dropping the old loanee_id column
ALTER TABLE loanee_loan_breakdown_entity
DROP COLUMN loanee_id;

-- Adding foreign key constraint for cohort_loanee_id
ALTER TABLE loanee_loan_breakdown_entity
    ADD CONSTRAINT fk_loanee_loan_breakdown_cohort_loanee
        FOREIGN KEY (cohort_loanee_id) REFERENCES cohort_loanee_entity(id);