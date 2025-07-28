-- Add the interest_incurred column with a default of 0.000000000000
ALTER TABLE program_loan_detail_entity
    ADD COLUMN interest_incurred NUMERIC(30, 12) DEFAULT 0.000000000000 NOT NULL;
