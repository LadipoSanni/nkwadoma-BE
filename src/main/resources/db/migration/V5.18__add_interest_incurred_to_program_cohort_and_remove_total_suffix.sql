-- === COHORT LOAN DETAIL ENTITY ===
ALTER TABLE cohort_loan_detail_entity
    RENAME COLUMN total_amount_requested TO amount_requested;

ALTER TABLE cohort_loan_detail_entity
    RENAME COLUMN total_outstanding_amount TO outstanding_amount;

ALTER TABLE cohort_loan_detail_entity
    RENAME COLUMN total_amount_received TO amount_received;

ALTER TABLE cohort_loan_detail_entity
    RENAME COLUMN total_amount_repaid TO amount_repaid;

ALTER TABLE cohort_loan_detail_entity
    ADD COLUMN interest_incurred NUMERIC(30, 12) DEFAULT 0.000000000000 NOT NULL;

-- === PROGRAM LOAN DETAIL ENTITY ===
ALTER TABLE program_loan_detail_entity
    RENAME COLUMN total_amount_requested TO amount_requested;

ALTER TABLE program_loan_detail_entity
    RENAME COLUMN total_outstanding_amount TO outstanding_amount;

ALTER TABLE program_loan_detail_entity
    RENAME COLUMN total_amount_received TO amount_received;

ALTER TABLE program_loan_detail_entity
    RENAME COLUMN total_amount_repaid TO amount_repaid;

ALTER TABLE program_loan_detail_entity
    ADD COLUMN interest_incurred NUMERIC(30, 12) DEFAULT 0.000000000000 NOT NULL;

-- === ORGANIZATION LOAN DETAIL ENTITY ===
ALTER TABLE organization_loan_detail_entity
    RENAME COLUMN total_amount_requested TO amount_requested;

ALTER TABLE organization_loan_detail_entity
    RENAME COLUMN total_outstanding_amount TO outstanding_amount;

ALTER TABLE organization_loan_detail_entity
    RENAME COLUMN total_amount_received TO amount_received;

ALTER TABLE organization_loan_detail_entity
    RENAME COLUMN total_amount_repaid TO amount_repaid;

ALTER TABLE organization_loan_detail_entity
    ADD COLUMN interest_incurred NUMERIC(30, 12) DEFAULT 0.000000000000 NOT NULL;
