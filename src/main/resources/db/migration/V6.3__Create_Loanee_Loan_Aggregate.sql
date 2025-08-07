CREATE TABLE IF NOT EXISTS loanee_loan_aggregate_entity (
    id VARCHAR(36) PRIMARY KEY,
    total_amount_outstanding DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    historical_debt DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    number_of_loans INT NOT NULL DEFAULT 0,
    loanee_id VARCHAR(36),
    FOREIGN KEY (loanee_id) REFERENCES loanee_entity(id)
    );