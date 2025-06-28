-- Create the cohort_loan_detail_entity table
CREATE TABLE if not exist cohort_loan_detail_entity (
                                           id VARCHAR(36) PRIMARY KEY,
                                           cohort_id VARCHAR(36) NOT NULL,
                                           total_amount_requested DECIMAL(19,2) DEFAULT 0.00,
                                           total_outstanding_amount DECIMAL(19,2) DEFAULT 0.00,
                                           total_amount_received DECIMAL(19,2) DEFAULT 0.00,
                                           FOREIGN KEY (cohort_id) REFERENCES cohort_entity(id)
);

-- Insert data into cohort_loan_detail_entity for all existing cohorts
INSERT INTO cohort_loan_detail_entity (id, cohort_id, total_amount_requested, total_outstanding_amount, total_amount_received)
SELECT
    UUID_GENERATE_V4(),
    c.id,
    COALESCE((
                 SELECT SUM(lld.amount_requested)
                 FROM cohort_loanee_entity cl
                          JOIN loanee_loan_detail_entity lld ON cl.loanee_loan_detail_id = lld.id
                 WHERE cl.cohort_id = c.id
             ), 0.0) AS total_amount_requested,
    0.0 AS total_outstanding_amount,
    0.0 AS total_amount_received
FROM cohort_entity c;