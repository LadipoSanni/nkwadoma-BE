-- Create main Loan Disbursement Rule Entity table
CREATE TABLE IF NOT EXISTS loan_disbursement_rule_entity (
                                                             id VARCHAR(255) PRIMARY KEY,
    loan_entity_id VARCHAR(255),
    disbursement_rule_entity_id VARCHAR(255),
    name VARCHAR(255),
    applied_by VARCHAR(255),
    interval VARCHAR(70),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    activation_status VARCHAR(50),
    date_applied TIMESTAMP,
    CONSTRAINT fk_loan_disbursement_rule_loan FOREIGN KEY (loan_entity_id)
    REFERENCES loan_entity (id) ON DELETE CASCADE,
    CONSTRAINT fk_loan_disbursement_rule_disbursement FOREIGN KEY (disbursement_rule_entity_id)
    REFERENCES disbursement_rule_entity (id) ON DELETE CASCADE
    );

-- Create separate table for percentage distribution
CREATE TABLE IF NOT EXISTS loan_disbursement_rule_percentage_distribution (
                                                                              loan_disbursement_rule_entity_id VARCHAR(255) NOT NULL,
    percentage_distribution DOUBLE PRECISION,
    CONSTRAINT fk_loan_disbursement_rule_distribution FOREIGN KEY (loan_disbursement_rule_entity_id)
    REFERENCES loan_disbursement_rule_entity (id) ON DELETE CASCADE
    );
