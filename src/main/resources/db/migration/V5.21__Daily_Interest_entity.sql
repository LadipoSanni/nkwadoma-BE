CREATE TABLE IF NOT EXISTS daily_interest_entity (
    id VARCHAR(36) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    interest DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    loanee_loan_detail_id VARCHAR(36),
    FOREIGN KEY (loanee_loan_detail_id) REFERENCES loanee_loan_detail_entity(id)
);
