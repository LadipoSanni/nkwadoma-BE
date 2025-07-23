BEGIN;

ALTER TABLE repayment_history_entity
    ADD COLUMN IF NOT EXISTS interest_incurred DECIMAL(19, 2) DEFAULT 0.0;

UPDATE repayment_history_entity
SET interest_incurred = 0.0
WHERE interest_incurred IS NULL;

ALTER TABLE repayment_history_entity
    ALTER COLUMN interest_incurred SET NOT NULL;

ALTER TABLE loanee_loan_detail_entity
    ADD COLUMN IF NOT EXISTS interest_rate DOUBLE PRECISION DEFAULT 0.0,
    ADD COLUMN IF NOT EXISTS interest_incurred DECIMAL(19, 2) DEFAULT 0.0;

COMMIT;