CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS loanee_loan_aggregate_entity (
    id VARCHAR(36) PRIMARY KEY,
    total_amount_outstanding DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    historical_debt DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    number_of_loans INT NOT NULL DEFAULT 0,
    loanee_id VARCHAR(36),
    FOREIGN KEY (loanee_id) REFERENCES loanee_entity(id)
    );


-- Insert aggregated loan data for each loanee into loanee_loan_aggregate_entity
INSERT INTO loanee_loan_aggregate_entity (
    id,
    historical_debt,
    total_amount_outstanding,
    number_of_loans,
    loanee_id
)
SELECT
    uuid_generate_v4(),    -- Generate a new UUID for the aggregate entity
    COALESCE(SUM(lld.amount_received), 0.00) AS historical_debt,
    COALESCE(SUM(lld.amount_outstanding), 0.00) AS total_amount_outstanding,
    COUNT(lld.id) AS number_of_loans,
    l.id AS loanee_id
FROM loanee_entity l
     JOIN cohort_loanee_entity cl ON l.id = cl.loanee_id
     JOIN loanee_loan_detail_entity lld ON cl.loanee_loan_detail_id = lld.id
GROUP BY l.id;