CREATE TABLE IF NOT EXISTS daily_interest_entity (
    id VARCHAR(36) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    interest DECIMAL(19,12) NOT NULL DEFAULT 0.00,
    loanee_loan_detail_id VARCHAR(36),
    FOREIGN KEY (loanee_loan_detail_id) REFERENCES loanee_loan_detail_entity(id)
);

CREATE TABLE IF NOT EXISTS monthly_interest_entity (
    id VARCHAR(36) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    interest DECIMAL(19,12) NOT NULL DEFAULT 0.00,
    loanee_loan_detail_id VARCHAR(36),
    FOREIGN KEY (loanee_loan_detail_id) REFERENCES loanee_loan_detail_entity(id)
);



-- Repayment History
ALTER TABLE repayment_history_entity
ALTER COLUMN amount_paid TYPE DECIMAL(19,8),
    ALTER COLUMN total_amount_repaid TYPE DECIMAL(19,8),
    ALTER COLUMN amount_outstanding TYPE DECIMAL(19,8),
    ALTER COLUMN interest_incurred TYPE DECIMAL(19,8);

-- Loanee Loan Detail
ALTER TABLE loanee_loan_detail_entity
ALTER COLUMN tuition_amount TYPE DECIMAL(19,8),
    ALTER COLUMN initial_deposit TYPE DECIMAL(19,8),
    ALTER COLUMN amount_requested TYPE DECIMAL(19,8),
    ALTER COLUMN amount_received TYPE DECIMAL(19,8),
    ALTER COLUMN amount_repaid TYPE DECIMAL(19,8),
    ALTER COLUMN amount_outstanding TYPE DECIMAL(19,8),
    ALTER COLUMN interest_incurred TYPE DECIMAL(19,8);

-- Cohort Loan Detail
ALTER TABLE cohort_loan_detail_entity
ALTER COLUMN amount_requested TYPE DECIMAL(19,8),
    ALTER COLUMN outstanding_amount TYPE DECIMAL(19,8),
    ALTER COLUMN amount_received TYPE DECIMAL(19,8),
    ALTER COLUMN amount_repaid TYPE DECIMAL(19,8),
    ALTER COLUMN interest_incurred TYPE DECIMAL(19,8);

-- Program Loan Detail
ALTER TABLE program_loan_detail_entity
ALTER COLUMN amount_requested TYPE DECIMAL(19,8),
    ALTER COLUMN outstanding_amount TYPE DECIMAL(19,8),
    ALTER COLUMN amount_received TYPE DECIMAL(19,8),
    ALTER COLUMN amount_repaid TYPE DECIMAL(19,8),
    ALTER COLUMN interest_incurred TYPE DECIMAL(19,8);

-- Organization Loan Detail
ALTER TABLE organization_loan_detail_entity
ALTER COLUMN amount_requested TYPE DECIMAL(19,8),
    ALTER COLUMN outstanding_amount TYPE DECIMAL(19,8),
    ALTER COLUMN amount_received TYPE DECIMAL(19,8),
    ALTER COLUMN amount_repaid TYPE DECIMAL(19,8),
    ALTER COLUMN interest_incurred TYPE DECIMAL(19,8);

