ALTER TABLE repayment_history_entity
    ADD COLUMN interest_incurred NUMERIC(30, 12);

-- Set the default BEFORE setting not null
ALTER TABLE repayment_history_entity
    ALTER COLUMN interest_incurred SET DEFAULT 0.000000000000;

-- Update existing rows to avoid nulls
UPDATE repayment_history_entity
SET interest_incurred = 0.000000000000
WHERE interest_incurred IS NULL;

-- Now enforce NOT NULL
ALTER TABLE repayment_history_entity
    ALTER COLUMN interest_incurred SET NOT NULL;





ALTER TABLE loanee_loan_detail_entity
    ADD COLUMN interest_rate DOUBLE PRECISION DEFAULT 0.0;

ALTER TABLE loanee_loan_detail_entity
    ADD COLUMN interest_incurred NUMERIC(19, 2) DEFAULT 0.0;
