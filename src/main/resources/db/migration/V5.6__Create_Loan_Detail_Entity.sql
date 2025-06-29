-- Enable UUID extension (safe to run multiple times)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the cohort_loan_detail_entity table with auto-generated UUID and unique cohort_id
CREATE TABLE IF NOT EXISTS cohort_loan_detail_entity (
                                                         id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cohort_id VARCHAR(36) NOT NULL UNIQUE,
    total_amount_requested DECIMAL(19,2) DEFAULT 0.00,
    total_outstanding_amount DECIMAL(19,2) DEFAULT 0.00,
    total_amount_received DECIMAL(19,2) DEFAULT 0.00,
    FOREIGN KEY (cohort_id) REFERENCES cohort_entity(id)
    );

-- Insert data into cohort_loan_detail_entity for all existing cohorts (skip existing cohort_id)
INSERT INTO cohort_loan_detail_entity (
    cohort_id,
    total_amount_requested,
    total_outstanding_amount,
    total_amount_received
)
SELECT
    c.id,
    COALESCE((
                 SELECT SUM(lld.amount_requested)
                 FROM cohort_loanee_entity cl
                          JOIN loanee_loan_detail_entity lld ON cl.loanee_loan_detail_id = lld.id
                 WHERE cl.cohort_id = c.id
             ), 0.0) AS total_amount_requested,
    COALESCE((
                 SELECT SUM(lo.amount_approved)
                 FROM cohort_loanee_entity cle
                          JOIN loan_referral_entity lre ON lre.cohort_loanee_id = cle.id
                          JOIN loan_request_entity lr ON lr.id = lre.id
                          JOIN loan_offer_entity lo ON lo.id = lr.id
                          JOIN loan_entity loan ON loan.loan_offer_id = lo.id
                 WHERE cle.cohort_id = c.id
                   AND lo.loan_offer_status = 'OFFERED'
                   AND loan.loan_status = 'PERFORMING'
             ), 0.0) AS total_outstanding_amount,
    COALESCE((
                 SELECT SUM(lo.amount_approved)
                 FROM cohort_loanee_entity cle
                          JOIN loan_referral_entity lre ON lre.cohort_loanee_id = cle.id
                          JOIN loan_request_entity lr ON lr.id = lre.id
                          JOIN loan_offer_entity lo ON lo.id = lr.id
                          JOIN loan_entity loan ON loan.loan_offer_id = lo.id
                 WHERE cle.cohort_id = c.id
                   AND lo.loan_offer_status = 'OFFERED'
                   AND loan.loan_status = 'PERFORMING'
             ), 0.0) AS total_amount_received
FROM cohort_entity c
WHERE NOT EXISTS (
    SELECT 1 FROM cohort_loan_detail_entity clde WHERE clde.cohort_id = c.id
);